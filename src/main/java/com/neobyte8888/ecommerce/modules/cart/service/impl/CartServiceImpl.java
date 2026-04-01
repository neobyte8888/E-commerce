package com.neobyte8888.ecommerce.modules.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neobyte8888.ecommerce.exception.BusinessException;
import com.neobyte8888.ecommerce.exception.ResourceNotFoundException;
import com.neobyte8888.ecommerce.modules.auth.entity.User;
import com.neobyte8888.ecommerce.modules.auth.repository.UserRepository;
import com.neobyte8888.ecommerce.modules.cart.dto.CartItemRequest;
import com.neobyte8888.ecommerce.modules.cart.dto.CartItemResponse;
import com.neobyte8888.ecommerce.modules.cart.dto.CartResponse;
import com.neobyte8888.ecommerce.modules.cart.entity.Cart;
import com.neobyte8888.ecommerce.modules.cart.entity.CartItem;
import com.neobyte8888.ecommerce.modules.cart.repository.CartRepository;
import com.neobyte8888.ecommerce.modules.cart.service.CartService;
import com.neobyte8888.ecommerce.modules.product.entity.Product;
import com.neobyte8888.ecommerce.modules.product.repository.ProductRepository;

@Service
public class CartServiceImpl implements CartService {

	private final UserRepository userRepository;
	private final CartRepository cartRepository;
	private final ProductRepository productRepository;

	public CartServiceImpl(UserRepository userRepository, CartRepository cartRepository,
			ProductRepository productRepository) {
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.productRepository = productRepository;
	}

	@Override
	@Transactional
	public CartResponse addToCart(CartItemRequest request, String userEmail) {

		// BƯỚC 1: LẤY DANH TÍNH & KHỞI TẠO LƯỜI BIẾNG (Lazy Initialization)
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng"));

		// Truy vấn giỏ hàng. Nếu chưa có -> Tạo mới và lưu luôn xuống DB.
		Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
			Cart newCart = new Cart(user);
			return cartRepository.save(newCart);
		});

		// BƯỚC 2: KIỂM TRA SẢN PHẨM
		// Nhờ @SQLRestriction, hàm findById này sẽ TỰ ĐỘNG BỎ QUA các sản phẩm
		// is_deleted = true.
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh"));

		// Tìm xem sản phẩm này đã có trong giỏ hàng chưa
		Optional<CartItem> existingItemOpt = cart.getItems().stream()
				.filter(item -> item.getProduct().getId().equals(product.getId())).findFirst();

		// BƯỚC 3 & 4: VALIDATE TỒN KHO VÀ CẬP NHẬT, Phải cộng dồn số lượng ĐANG CÓ
		// TRONG GIỎ với số lượng MỚI THÊM để validate tồn kho.
		int newQuantity = request.getQuantity();
		if (existingItemOpt.isPresent()) {
			newQuantity += existingItemOpt.get().getQuantity();
		}

		if (newQuantity > product.getStock()) {
			throw new BusinessException("Sản phẩm không đủ tồn kho. Tồn kho hiện tại chỉ còn: " + product.getStock());
		}

		// Cập nhật hoặc Thêm mới
		if (existingItemOpt.isPresent()) {
			existingItemOpt.get().setQuantity(newQuantity);
		} else {
			CartItem newItem = new CartItem(cart, product, request.getQuantity());
			cart.addItem(newItem); // Dùng Helper method ở Sprint 18 để đồng bộ quan hệ 2 chiều
		}

		// Lưu giỏ hàng (Nhờ CascadeType.ALL, Hibernate sẽ tự động UPDATE/INSERT
		// cart_items)
		Cart savedCart = cartRepository.save(cart);

		return mapToResponse(savedCart);

	}
	
	// XEM GIỎ HÀNG
	@Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng"));

        // Tìm giỏ hàng. Nếu user chưa từng có giỏ, trả về một giỏ rỗng ảo (không save xuống DB để tránh rác)
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart emptyCart = new Cart(user);
                    return emptyCart; 
                });

        // Hàm mapper dưới đây sẽ MÓC SANG bảng Product để lấy giá và tồn kho REAL-TIME
        return mapToResponse(cart);
    }
	
	//XOÁ SẢN PHẨM KHỎI GIỎ HÀNG
	@Override
    @Transactional
    public CartResponse removeCartItem(Long itemId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng trống"));

        // Tìm item cần xóa trong giỏ
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại trong giỏ hàng"));

        // Xóa khỏi List. Nhờ orphanRemoval = true, Hibernate sẽ tự động DELETE dòng này dưới DB
        cart.removeItem(itemToRemove);
        
        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }
	
	// ==========================================
    // CẬP NHẬT SỐ LƯỢNG (Kèm chống IDOR)
    // ==========================================
    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(Long itemId, Integer quantity, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng trống"));

        // CHỐNG HACK (IDOR): Chỉ tìm item NẰM TRONG giỏ hàng của user này. 
        // Nếu truyền ID của user khác, hàm findFirst() sẽ ném lỗi ngay.
        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại trong giỏ hàng của bạn"));

        // Nếu quantity = 0 -> Gọi logic Xóa
        if (quantity == 0) {
            cart.removeItem(itemToUpdate);
        } else {
            // Validate lại tồn kho (Tư duy Senior)
            Product product = itemToUpdate.getProduct();
            if (quantity > product.getStock()) {
                throw new BusinessException("Sản phẩm không đủ tồn kho. Tồn kho hiện tại chỉ còn: " + product.getStock());
            }
            // Cập nhật số lượng mới
            itemToUpdate.setQuantity(quantity);
        }

        return mapToResponse(cartRepository.save(cart));
    }

    // ==========================================
    // SPRINT 22: DỌN SẠCH GIỎ HÀNG (Clear Cart)
    // ==========================================
    @Override
    @Transactional
    public CartResponse clearCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng trống"));

        // Xóa sạch mảng items. 
        // Nhờ cấu hình orphanRemoval = true ở Entity Cart, Hibernate sẽ tự động gọi lệnh DELETE các dòng dưới DB.
        cart.getItems().clear();

        return mapToResponse(cartRepository.save(cart));
    }

	// ==========================================
	// HÀM MAPPER: TÍNH TOÁN REAL-TIME CHECK TỒN KHO
	// ==========================================
	private CartResponse mapToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());

        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        // Nếu giỏ hàng rỗng
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            response.setItems(itemResponses);
            response.setTotalPrice(totalPrice);
            return response;
        }

        for (CartItem item : cart.getItems()) {
            CartItemResponse itemRes = new CartItemResponse();
            itemRes.setId(item.getId());
            
            Product product = item.getProduct();
            itemRes.setProductId(product.getId());
            itemRes.setProductName(product.getName());
            itemRes.setProductSlug(product.getSlug());
            itemRes.setImageUrl(product.getImageUrl());
            
            // 1. Tính toán giá Real-time
            itemRes.setPrice(product.getPrice());
            itemRes.setQuantity(item.getQuantity());
            
            // Cảnh báo biến động tồn kho
            // Nếu quantity trong giỏ > stock hiện tại trong kho -> Bật cờ True
            if (item.getQuantity() > product.getStock()) {
                itemRes.setIsStockAltered(true);
            } else {
                itemRes.setIsStockAltered(false);
            }
            
            // 3. Subtotal = Giá mới nhất * Số lượng
            BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            itemRes.setSubTotal(subTotal);
            
            itemResponses.add(itemRes);
            totalPrice = totalPrice.add(subTotal);
        }

        response.setItems(itemResponses);
        response.setTotalPrice(totalPrice);
        return response;
    }

}
