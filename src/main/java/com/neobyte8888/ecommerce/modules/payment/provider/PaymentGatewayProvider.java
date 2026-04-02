package com.neobyte8888.ecommerce.modules.payment.provider;

import com.neobyte8888.ecommerce.modules.payment.entity.Payment;

public interface PaymentGatewayProvider {
    
    // Hàm cốt lõi: Nhận vào thông tin Payment và trả về một đường link URL
    // để chuyển hướng (redirect) người dùng sang trang của Ngân hàng.
    String generatePaymentUrl(Payment payment);
    
    // Định danh cổng thanh toán này là gì (VNPAY, STRIPE...)
    String getProviderName();
}