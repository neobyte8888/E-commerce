package com.neobyte8888.ecommerce.modules.auth.service;

import com.neobyte8888.ecommerce.modules.auth.dto.LoginRequest;
import com.neobyte8888.ecommerce.modules.auth.dto.RegisterRequest;

public interface AuthService {
	
	// Hàm đăng ký tài khoản
    void register(RegisterRequest request);
    
    // Hàm xử lý đăng nhập trả về chuỗi JWT
    String login(LoginRequest loginRequest);
}