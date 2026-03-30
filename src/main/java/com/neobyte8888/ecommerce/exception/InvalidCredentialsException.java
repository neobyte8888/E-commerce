package com.neobyte8888.ecommerce.exception;

//Sai mật khẩu, Token hết hạn...
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}