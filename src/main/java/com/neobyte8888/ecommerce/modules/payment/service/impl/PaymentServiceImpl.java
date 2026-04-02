package com.neobyte8888.ecommerce.modules.payment.service.impl;

import com.neobyte8888.ecommerce.exception.BusinessException;
import com.neobyte8888.ecommerce.exception.ResourceNotFoundException;
import com.neobyte8888.ecommerce.modules.auth.entity.User;
import com.neobyte8888.ecommerce.modules.auth.repository.UserRepository;
import com.neobyte8888.ecommerce.modules.order.entity.Order;
import com.neobyte8888.ecommerce.modules.order.entity.OrderItem;
import com.neobyte8888.ecommerce.modules.order.enums.OrderStatus;
import com.neobyte8888.ecommerce.modules.order.repository.OrderRepository;
import com.neobyte8888.ecommerce.modules.payment.controller.PaymentController;
import com.neobyte8888.ecommerce.modules.payment.dto.PaymentUrlResponse;
import com.neobyte8888.ecommerce.modules.payment.entity.Payment;
import com.neobyte8888.ecommerce.modules.payment.enums.PaymentProvider;
import com.neobyte8888.ecommerce.modules.payment.enums.PaymentStatus;
import com.neobyte8888.ecommerce.modules.payment.provider.PaymentGatewayProvider;
import com.neobyte8888.ecommerce.modules.payment.repository.PaymentRepository;
import com.neobyte8888.ecommerce.modules.payment.service.PaymentService;
import com.neobyte8888.ecommerce.modules.product.entity.Product;
import com.neobyte8888.ecommerce.modules.product.repository.ProductRepository;
import com.neobyte8888.ecommerce.util.HmacUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
	
	private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayProvider paymentGatewayProvider;
    private final ProductRepository productRepository;
    
    @Value("${payment.vnpay.secret-key}")
    private String vnpaySecretKey;

    public PaymentServiceImpl(UserRepository userRepository, 
                              OrderRepository orderRepository, 
                              PaymentRepository paymentRepository, 
                              PaymentGatewayProvider paymentGatewayProvider,
                              ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGatewayProvider = paymentGatewayProvider;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public PaymentUrlResponse generatePaymentUrl(Long orderId, String userEmail) {
        
        // 1. Xác thực danh tính User
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // 2. CHỐNG IDOR: Query Order bằng orderId VÀ userId
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại hoặc không thuộc về bạn"));

        // 3. (Validate Status): Chặn thanh toán lại các đơn không hợp lệ
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Đơn hàng không ở trạng thái chờ thanh toán. Trạng thái hiện tại: " + order.getStatus());
        }

        // 4. Xử lý Record Payment (Chống vi phạm UNIQUE INDEX)
        Optional<Payment> existingPaymentOpt = paymentRepository.findByOrderId(order.getId());
        Payment payment;
        
        if (existingPaymentOpt.isPresent()) {
            payment = existingPaymentOpt.get();
            // Nếu đã thanh toán thành công trước đó (dù Order chưa kịp cập nhật vì lý do nào đó)
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                throw new BusinessException("Giao dịch này đã được thanh toán thành công trước đó.");
            }
            // Nếu PENDING hoặc FAILED, ta có thể cho phép thanh toán lại (tái sử dụng record này)
            payment.setAmount(order.getTotalAmount()); // Đảm bảo số tiền luôn chuẩn xác
            payment.setStatus(PaymentStatus.PENDING);
        } else {
            // Tạo mới record Payment
            payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(order.getTotalAmount());
            payment.setProvider(PaymentProvider.VNPAY); // Tạm thời hardcode VNPAY, sau này có thể nhận từ Request
            payment.setStatus(PaymentStatus.PENDING);
        }

        // Lưu xuống Database. Lúc này payment đã được sinh ra một UUID (Khóa chính)
        payment = paymentRepository.save(payment);

        // 5. Gọi Provider (MockVnPayService) để sinh URL
        String url = paymentGatewayProvider.generatePaymentUrl(payment);

        return new PaymentUrlResponse(url);
    }
    
    // ==========================================
    // BẢO MẬT WEBHOOK (XÁC MINH CHỮ KÝ SỐ)
    // ==========================================
    @Override
    public void verifyWebhookSignature(Map<String, String> payload) {
        // 1. Lấy chữ ký do Ngân hàng gửi đến
        String bankSignature = payload.get("vnp_SecureHash");
        if (bankSignature == null || bankSignature.isEmpty()) {
            throw new BusinessException("Webhook không hợp lệ: Thiếu chữ ký điện tử (vnp_SecureHash)");
        }

        // 2. Tách chữ ký ra khỏi Map để chuẩn bị băm các trường còn lại
        Map<String, String> hashData = new HashMap<>(payload);
        hashData.remove("vnp_SecureHash");
        hashData.remove("vnp_SecureHashType");

        // 3. Sắp xếp các tham số theo thứ tự A-Z (Yêu cầu bắt buộc của VNPay và hầu hết cổng thanh toán)
        List<String> fieldNames = new ArrayList<>(hashData.keySet());
        Collections.sort(fieldNames);

        // 4. Tạo chuỗi dữ liệu gốc (Format: key1=value1&key2=value2...)
        StringBuilder signDataBuilder = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = hashData.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                signDataBuilder.append(fieldName).append("=").append(fieldValue).append("&");
            }
        }
        
        // Xóa dấu "&" thừa ở cuối chuỗi
        if (signDataBuilder.length() > 0) {
            signDataBuilder.deleteCharAt(signDataBuilder.length() - 1);
        }
        
        String signData = signDataBuilder.toString();

        // 5. Tự băm (Hash) chuỗi bằng Secret Key của Server
        String serverSignature = HmacUtils.calculateHmacSha256(signData, vnpaySecretKey);

        // 6. SO SÁNH SINH TỬ
        if (!serverSignature.equalsIgnoreCase(bankSignature)) {
            // Ghi log cảnh báo nghiêm trọng
        	log.error("[SECURITY ALERT] - Phát hiện giả mạo Webhook! Chữ ký không khớp.");
        	log.error("Data: " + signData);
        	log.error("Bank Hash: " + bankSignature);
        	log.error("Server Hash: " + serverSignature);            
            // Đuổi cổ request
            throw new BusinessException("Chữ ký số không hợp lệ. Từ chối yêu cầu!");
        }
        
        log.info("[WEBHOOK] Chữ ký hợp lệ. Request đến từ Ngân hàng chuẩn.");  
    }
    
    // ==========================================
    // CHỐT SỔ GIAO DỊCH & IDEMPOTENCY
    // ==========================================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processWebhookResult(Map<String, String> payload) {
        
        // 1. Trích xuất thông tin từ Payload của VNPAY
        String txnRef = payload.get("vnp_TxnRef"); // Chính là UUID của bảng Payment
        String responseCode = payload.get("vnp_ResponseCode"); // "00" là thành công
        String bankTransactionNo = payload.get("vnp_TransactionNo"); // Mã GD của ngân hàng để đối soát

        // 2. Tìm kiếm Giao dịch và Đơn hàng tương ứng
        UUID paymentId = UUID.fromString(txnRef);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy giao dịch với ID: " + txnRef));

        Order order = payment.getOrder();

        // IDEMPOTENCY (TÍNH LŨY ĐẲNG)
        // Nếu giao dịch ĐÃ ĐƯỢC XỬ LÝ TRƯỚC ĐÓ (Không còn là PENDING), ta BỎ QUA lập tức.
        if (payment.getStatus() != PaymentStatus.PENDING || order.getStatus() != OrderStatus.PENDING) {
            log.warn("[IDEMPOTENCY] Giao dịch " + txnRef + " đã được xử lý trước đó. Bỏ qua để tránh lặp logic.");   
            return; // Return thẳng, không ném Exception để Controller vẫn trả về 200 OK cho VNPAY
        }

        // 4. Lưu lại mã đối soát của Ngân hàng
        payment.setProviderTransactionId(bankTransactionNo);

        // 5. RẼ NHÁNH KẾT QUẢ THANH TOÁN
        if ("00".equals(responseCode)) {
            // =====================================
            // KỊCH BẢN 1: THANH TOÁN THÀNH CÔNG
            // =====================================
            payment.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.PAID);
            log.warn("[PAYMENT] Đơn hàng " + order.getId() + " đã thanh toán THÀNH CÔNG!"); 

        } else {
            // =====================================
            // KỊCH BẢN 2: THANH TOÁN THẤT BẠI / HỦY BỎ
            // =====================================
            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.CANCELLED);
            log.warn("[PAYMENT] Đơn hàng " + order.getId() + " thanh toán THẤT BẠI. Đang tiến hành hoàn kho..."); 

            // HOÀN TRẢ TỒN KHO (ROLLBACK INVENTORY)
            // Kế thừa chặt chẽ logic Hủy đơn
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }

        // 6. Lưu xuống Database (Nhờ @Transactional, nếu có lỗi thì mọi thứ sẽ tự Rollback)
        paymentRepository.save(payment);
        orderRepository.save(order);
    }
}