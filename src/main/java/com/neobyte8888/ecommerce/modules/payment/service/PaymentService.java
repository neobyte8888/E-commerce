package com.neobyte8888.ecommerce.modules.payment.service;

import java.util.Map;

import com.neobyte8888.ecommerce.modules.payment.dto.PaymentUrlResponse;

public interface PaymentService {
    PaymentUrlResponse generatePaymentUrl(Long orderId, String userEmail);
    
    // Kiểm tra chữ ký Webhook
    void verifyWebhookSignature(Map<String, String> payload);
    // Xử lý kết quả giao dịch và Idempotency
    void processWebhookResult(Map<String, String> payload);
}