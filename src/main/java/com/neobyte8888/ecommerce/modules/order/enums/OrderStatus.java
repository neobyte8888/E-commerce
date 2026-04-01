package com.neobyte8888.ecommerce.modules.order.enums;

public enum OrderStatus {
    PENDING,    // Chờ thanh toán
    PAID,       // Đã thanh toán
    SHIPPED,   // Đang giao hàng
    DELIVERED,  // Đã giao thành công
    CANCELLED   // Đã hủy
}