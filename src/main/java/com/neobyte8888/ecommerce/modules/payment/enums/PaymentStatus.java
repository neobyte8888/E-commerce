package com.neobyte8888.ecommerce.modules.payment.enums;

public enum PaymentStatus {
    PENDING,    // Đang chờ khách quét mã/nhập thẻ
    SUCCESS,    // Thanh toán thành công (Tiền đã vào tài khoản công ty)
    FAILED      // Thanh toán thất bại (Sai mã OTP, hết tiền, hủy giao dịch...)
}