package com.neobyte8888.ecommerce.modules.order.service.impl;

import org.springframework.data.domain.Page;
 import org.springframework.data.domain.PageRequest;
 import org.springframework.data.domain.Pageable;
 import org.springframework.data.domain.Sort;
 import java.util.List;
 import java.util.stream.Collectors;

import com.neobyte8888.ecommerce.common.PageResponse;
import com.neobyte8888.ecommerce.exception.BusinessException;
import com.neobyte8888.ecommerce.exception.ResourceNotFoundException;
import com.neobyte8888.ecommerce.modules.auth.entity.User;
import com.neobyte8888.ecommerce.modules.auth.repository.UserRepository;
import com.neobyte8888.ecommerce.modules.cart.entity.Cart;
import com.neobyte8888.ecommerce.modules.cart.entity.CartItem;
import com.neobyte8888.ecommerce.modules.cart.repository.CartRepository;
import com.neobyte8888.ecommerce.modules.order.dto.CheckoutRequest;
import com.neobyte8888.ecommerce.modules.order.dto.OrderItemResponse;
import com.neobyte8888.ecommerce.modules.order.dto.OrderResponse;
import com.neobyte8888.ecommerce.modules.order.entity.Order;
import com.neobyte8888.ecommerce.modules.order.entity.OrderItem;
import com.neobyte8888.ecommerce.modules.order.enums.OrderStatus;
import com.neobyte8888.ecommerce.modules.order.repository.OrderRepository;
import com.neobyte8888.ecommerce.modules.order.service.OrderService;
import com.neobyte8888.ecommerce.modules.product.entity.Product;
import com.neobyte8888.ecommerce.modules.product.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    // Constructor Injection
    public OrderServiceImpl(UserRepository userRepository, CartRepository cartRepository, 
                            ProductRepository productRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    // Khóa chặt bằng Transaction. Bất kỳ lỗi nào (kể cả Runtime hay Checked Exception) 
    // văng ra đều sẽ kích hoạt Rollback toàn bộ các lệnh UPDATE/INSERT trước đó.
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(CheckoutRequest request, String userEmail) {
        
        // 1. Lấy danh tính User và Giỏ hàng
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng không tồn tại"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessException("Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm trước khi thanh toán.");
        }
        

        // 2. Khởi tạo Đơn hàng (Order)
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        // 3. VÒNG LẶP SINH TỬ: Trừ kho & Chốt đơn
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int quantityToBuy = cartItem.getQuantity();

            // 3.1. Kiểm tra tồn kho (Tránh Overselling)
            if (product.getStock() < quantityToBuy) {
                // Nếu văng lỗi ở đây, Transaction sẽ hủy bỏ TẤT CẢ mọi thay đổi của các vòng lặp trước đó!
                throw new BusinessException("Sản phẩm '" + product.getName() + "' không đủ số lượng. Tồn kho hiện tại: " + product.getStock());
            }

            // 3.2. Thực hiện trừ kho
            product.setStock(product.getStock() - quantityToBuy);
            productRepository.save(product); // Cập nhật lại số lượng mới xuống DB

            // 3.3. Lấy giá tiền hiện tại (Real-time) và cộng dồn
            BigDecimal currentPrice = product.getPrice();
            BigDecimal subTotal = currentPrice.multiply(BigDecimal.valueOf(quantityToBuy));
            calculatedTotal = calculatedTotal.add(subTotal);

            // 3.4. Chuyển CartItem thành OrderItem (ĐÓNG BĂNG DỮ LIỆU)
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantityToBuy);
            
            // Lưu cứng giá tiền lúc mua để bảo toàn lịch sử giao dịch
            orderItem.setPrice(currentPrice); 

            // Gắn OrderItem vào Order
            order.addOrderItem(orderItem);
        }

        // 4. Chốt tổng tiền an toàn và Lưu đơn hàng
        order.setTotalAmount(calculatedTotal);
        // Nhờ CascadeType.ALL, khi ta save(order), Hibernate tự động save luôn cả list orderItems vào DB.
        Order savedOrder = orderRepository.save(order);

        // ==========================================
        // 5. DỌN DẸP GIỎ HÀNG (CLEANUP)
        // ==========================================
        // Xóa sạch mảng items của entity Cart hiện tại.
        // Cấu hình orphanRemoval = true sẽ tự động DELETE các dòng cart_items dưới DB.
        cart.getItems().clear();
        cartRepository.save(cart);

        // 6. Đóng gói kết quả trả về
        OrderResponse response = new OrderResponse();
        response.setId(savedOrder.getId());
        response.setTotalAmount(savedOrder.getTotalAmount());
        response.setStatus(savedOrder.getStatus());
        response.setShippingAddress(savedOrder.getShippingAddress());
        response.setPaymentMethod(savedOrder.getPaymentMethod());
        
        // Lấy thời gian tạo đơn hàng thực tế từ DB
        response.setCreatedAt(savedOrder.getCreatedAt());

        return response;
    }
    
    // ==========================================
    // LẤY LỊCH SỬ ĐƠN HÀNG (PHÂN TRANG)
    // ==========================================
    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getMyOrders(String userEmail, int pageNo, int pageSize, String sortBy, String sortDir) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // Truy vấn phân trang
        Page<Order> orderPage = orderRepository.findByUserId(user.getId(), pageable);

        List<OrderResponse> content = orderPage.getContent().stream()
                .map(this::mapToResponse) // Dùng hàm helper bên dưới
                .collect(Collectors.toList());

        PageResponse<OrderResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(content);
        pageResponse.setPageNo(orderPage.getNumber());
        pageResponse.setPageSize(orderPage.getSize());
        pageResponse.setTotalElements(orderPage.getTotalElements());
        pageResponse.setTotalPages(orderPage.getTotalPages());
        pageResponse.setLast(orderPage.isLast());

        return pageResponse;
    }

    // ==========================================
    // LẤY CHI TIẾT 1 ĐƠN HÀNG (CHỐNG IDOR)
    // ==========================================
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getMyOrderDetail(Long orderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // Truy vấn kèm User ID. Khóa chặt bảo mật IDOR.
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng, hoặc đơn hàng không thuộc về bạn"));

        return mapToResponse(order);
    }
    
    // ==========================================
    // ADMIN CẬP NHẬT TRẠNG THÁI (STATE MACHINE & ROLLBACK INVENTORY)
    // ==========================================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        OrderStatus currentStatus = order.getStatus();

        // 1. STATE MACHINE: Ngăn chặn các luồng chuyển đổi phi logic (Tư duy Senior)
        if (currentStatus == OrderStatus.DELIVERED) {
            throw new BusinessException("Không thể thay đổi trạng thái của đơn hàng đã giao thành công.");
        }
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new BusinessException("Đơn hàng này đã bị hủy, không thể phục hồi trạng thái.");
        }
        if (currentStatus == newStatus) {
            throw new BusinessException("Trạng thái mới không được trùng với trạng thái hiện tại.");
        }

        // 2. LOGIC HOÀN KHO (ROLLBACK INVENTORY) KHI HỦY ĐƠN
        // Nếu chuyển sang CANCELLED, phải lặp qua từng món hàng để trả lại số lượng vào kho
        if (newStatus == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                // Cộng lại số lượng tồn kho
                product.setStock(product.getStock() + item.getQuantity());
                // Lưu lại sự thay đổi của Product
                productRepository.save(product);
            }
        }

        // 3. Cập nhật trạng thái mới và lưu Đơn hàng
        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        // Trả về DTO cập nhật mới nhất (Tái sử dụng hàm helper ở Sprint 27)
        return mapToResponse(savedOrder);
    }

    // ==========================================
    // HÀM HELPER: MAP ORDER SANG ORDER_RESPONSE
    // ==========================================
    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setShippingAddress(order.getShippingAddress());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setCreatedAt(order.getCreatedAt());

        // Map danh sách items
        if (order.getOrderItems() != null) {
            List<OrderItemResponse> itemResponses = order.getOrderItems().stream().map(item -> {
                OrderItemResponse itemRes = new OrderItemResponse();
                Product product = item.getProduct();
                
                itemRes.setProductId(product.getId());
                itemRes.setProductName(product.getName());
                itemRes.setImageUrl(product.getImageUrl());
                itemRes.setQuantity(item.getQuantity());
                
                // Lấy giá cứng từ OrderItem (KHÔNG PHẢI TỪ PRODUCT)
                itemRes.setPrice(item.getPrice()); 
                itemRes.setSubTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                
                return itemRes;
            }).collect(Collectors.toList());
            
            response.setItems(itemResponses);
        }
        
        return response;
    }
}