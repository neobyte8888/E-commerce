package com.neobyte8888.ecommerce.modules.auth.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neobyte8888.ecommerce.exception.BusinessException;
import com.neobyte8888.ecommerce.modules.auth.dto.LoginRequest;
import com.neobyte8888.ecommerce.modules.auth.dto.RegisterRequest;
import com.neobyte8888.ecommerce.modules.auth.entity.Role;
import com.neobyte8888.ecommerce.modules.auth.entity.User;
import com.neobyte8888.ecommerce.modules.auth.repository.RoleRepository;
import com.neobyte8888.ecommerce.modules.auth.repository.UserRepository;
import com.neobyte8888.ecommerce.modules.auth.security.JwtTokenProvider;
import com.neobyte8888.ecommerce.modules.auth.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService{

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    // Constructor Injection (Thay thế @Autowired - Best Practice)
    public AuthServiceImpl(UserRepository userRepository, 
    						RoleRepository roleRepository, 
    						PasswordEncoder passwordEncoder,
    						AuthenticationManager authenticationManager,
    						JwtTokenProvider jwtTokenProvider) {
    	this.userRepository = userRepository;
    	this.roleRepository = roleRepository;
    	this.passwordEncoder = passwordEncoder;
    	this.authenticationManager = authenticationManager;
    	this.jwtTokenProvider = jwtTokenProvider;
    }
	
	@Override
	@Transactional
	public void register(RegisterRequest request) {
		// 1. Kiểm tra email trùng lặp
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("Email này đã được sử dụng. Vui lòng chọn email khác.");
		}
		
		// 2. Lấy role mặc định (ROLE_USER)
		Role userRole = roleRepository.findByName("ROLE_USER")
				.orElseThrow(() -> new BusinessException("Lỗi hệ thống: Không tìm thấy quyền ROLE_USER"));
		
		// 3. Khởi tạo User Entity
		User newUser = new User();
		newUser.setEmail(request.getEmail());
		newUser.setFullName(request.getFullName());
		
		// Băm mật khẩu. KHÔNG BAO GIỜ: newUser.setPassword(request.getPassword())
		newUser.setPassword(passwordEncoder.encode(request.getPassword()));
		
		//Gán quyền
		newUser.getRoles().add(userRole);
		
		//4. Lưu vào database
		userRepository.save(newUser);
		
	}

	@Override
	public String login(LoginRequest loginRequest) {
		// 1. Dùng AuthenticationManager (đã cấu hình ở Sprint 10) để xác thực
        // Nó sẽ tự động gọi CustomUserDetailsService -> Lấy User từ DB -> So sánh mật khẩu đã băm (BCrypt)
        // Nếu sai mật khẩu hoặc sai email, nó tự động ném ra Exception (BadCredentialsException)
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getEmail(),
						loginRequest.getPassword()
				)
		);
		
		// 2. Nếu code chạy được xuống đây nghĩa là mật khẩu ĐÚNG. 
        // Lưu Authentication vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Gọi cỗ máy (Sprint 9) để in thẻ JWT trả về
        return jwtTokenProvider.generateToken(authentication);
	}

}
