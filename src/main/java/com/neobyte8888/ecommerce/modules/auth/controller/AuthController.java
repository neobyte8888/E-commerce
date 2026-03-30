package com.neobyte8888.ecommerce.modules.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neobyte8888.ecommerce.common.ApiResponse;
import com.neobyte8888.ecommerce.modules.auth.dto.JwtAuthResponse;
import com.neobyte8888.ecommerce.modules.auth.dto.LoginRequest;
import com.neobyte8888.ecommerce.modules.auth.dto.RegisterRequest;
import com.neobyte8888.ecommerce.modules.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	//Annotation @Valid CỰC KỲ QUAN TRỌNG. Thiếu nó, các luật @NotBlank, @Email ở DTO sẽ bị Spring bỏ qua.
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequest){
		
		authService.register(registerRequest);
		
		// Trả về đúng định dạng ApiResponse với status 201 Created
		ApiResponse<Void> response = new ApiResponse<Void>(
				HttpStatus.CREATED.value(),
				"Đăng ký tài khoản thành công!"
		);
		
		return new ResponseEntity<ApiResponse<Void>>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<JwtAuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest){
		//Gọi service đăng nhập và trả về chuỗi token
		String token = authService.login(loginRequest);
		
		// Bọc chuỗi Token vào DTO
		JwtAuthResponse jwtAuthResponse = new JwtAuthResponse(token);
		
		// Trả về Json đúng định dang chuẩn
		ApiResponse<JwtAuthResponse> apiResponse = new ApiResponse<JwtAuthResponse>(
				HttpStatus.OK.value(),
				"Đăng nhập thành công!",
				jwtAuthResponse
		);
		
		
		return ResponseEntity.ok(apiResponse);
	}

}
