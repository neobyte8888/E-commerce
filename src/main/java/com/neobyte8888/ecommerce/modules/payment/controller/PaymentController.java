package com.neobyte8888.ecommerce.modules.payment.controller;

import com.neobyte8888.ecommerce.common.ApiResponse;
import com.neobyte8888.ecommerce.modules.payment.dto.PaymentUrlResponse;
import com.neobyte8888.ecommerce.modules.payment.service.PaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
	
	private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ==========================================
    // API TẠO LINK THANH TOÁN
    // ==========================================
    @PostMapping("/generate-url/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PaymentUrlResponse>> generatePaymentUrl(@PathVariable Long orderId) {
        
        // Trích xuất email từ JWT
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Gọi Service
        PaymentUrlResponse data = paymentService.generatePaymentUrl(orderId, userEmail);
        
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Tạo đường dẫn thanh toán thành công", 
                data
        ));
    }
    
    // ==========================================
    // WEBHOOK LẮNG NGHE KẾT QUẢ THANH TOÁN
    // =========================================
    @PostMapping("/webhook/vnpay")
    public ResponseEntity<ApiResponse<String>> handleVnPayWebhook(@RequestParam java.util.Map<String, String> payload) {
        
        log.info("[WEBHOOK] Nhận được tín hiệu từ cổng thanh toán...");

        // Bước 1: XÁC THỰC CHỮ KÝ
        paymentService.verifyWebhookSignature(payload);

        // Bước 2: XỬ LÝ NGHIỆP VỤ & LŨY ĐẲNG
        paymentService.processWebhookResult(payload);

        // Trả về 200 OK cho VNPay (Báo hiệu hệ thống đã ghi nhận thành công, ngừng gửi lại)
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Đã xử lý tín hiệu Webhook thành công", 
                "OK"
        ));
    }
}