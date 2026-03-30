package com.neobyte8888.ecommerce.exception;

//Dùng khi tìm không thấy Product, User, Order...
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}