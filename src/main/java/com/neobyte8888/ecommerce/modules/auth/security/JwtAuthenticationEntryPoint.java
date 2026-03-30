package com.neobyte8888.ecommerce.modules.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobyte8888.ecommerce.common.ApiResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    // Inject ObjectMapper (thư viện parse JSON mặc định của Spring)
    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 1. TƯ DUY SENIOR: Ép kiểu Content-Type trả về là JSON thay vì text/html mặc định
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 2. Tái sử dụng chuẩn ApiResponse (Sprint 3)
        ApiResponse<Object> apiResponse = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                "Bạn chưa đăng nhập hoặc phiên đăng nhập đã hết hạn. Lỗi: " + authException.getMessage()
        );

        // 3. Tự tay ghi đè chuỗi JSON vào luồng trả về (Response Stream)
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}