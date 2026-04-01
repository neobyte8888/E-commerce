package com.neobyte8888.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.neobyte8888.ecommerce.modules.auth.security.JwtAuthenticationEntryPoint;
import com.neobyte8888.ecommerce.modules.auth.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity // Kích hoạt tính năng Bảo mật web của Spring Security
@EnableMethodSecurity // <--- BẮT BUỘC THÊM DÒNG NÀY ĐỂ @PreAuthorize HOẠT ĐỘNG
public class SecurityConfig {
	
	private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
    // Constructor Injection các trạm gác vào SecurityConfig
    public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
	
	/**
     * Bean cấu hình lõi của Spring Security (Thay thế WebSecurityConfigurerAdapter cũ)
     */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// 1. Tắt CSRF (Cross-Site Request Forgery)
	        // Vì chúng ta đang build REST API dùng JWT, không dùng Cookie/Session của trình duyệt,
	        // nên tấn công CSRF là không thể xảy ra. Việc tắt đi giúp API không bị block oan.
	        // Cú pháp chuẩn của Spring Security 6.x/7.x
			.csrf(AbstractHttpConfigurer::disable)
			
			// Có thể cấu hình CORS ở đây nếu Frontend (React/Vue) chạy ở port khác gọi sang
            // .cors(Customizer.withDefaults()) 

            // 2. Thiết lập cơ chế STATELESS (Không trạng thái)
            // Ép Spring Security KHÔNG ĐƯỢC PHÉP tạo Session lưu thông tin đăng nhập.
            // Mỗi request đều là một request hoàn toàn mới và độc lập.
			.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			
			// 3. Phân quyền Endpoint (Trạm gác)
			.authorizeHttpRequests(auth -> auth
	                // Cho phép tất cả mọi người (kể cả chưa đăng nhập) truy cập vào các API Auth (Đăng ký, Đăng nhập)
	                .requestMatchers("/api/v1/auth/**").permitAll()
	                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
	                .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll() // Cho phép xem danh mục không cần Token
	                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll() // Cho phép xem danh mục không cần Token
	                // MỞ RỘNG TƯƠNG LAI: Nếu có Swagger UI để test API, ta cũng sẽ mở ở đây
	                // .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

	                // TẤT CẢ các request còn lại chạy vào hệ thống BẮT BUỘC phải được xác thực (Đã đăng nhập)
	                .anyRequest().authenticated()
			);

		// Cắm Trạm soát vé JWT vào TRƯỚC Trạm soát vé mặc định của Spring Security
        // Để nó chặn request, đọc JWT và nạp thông tin trước khi Spring Security kịp phán xét.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
		return http.build();
		
	}
	
	// Khởi tạo AuthenticationManager để dùng cho Sprint 11 (API Login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
