package com.neobyte8888.ecommerce.exception;

//Dùng cho các lỗi logic như: Hết hàng, Giỏ hàng trống, Trùng email...
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}