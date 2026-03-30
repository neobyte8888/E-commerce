package com.neobyte8888.ecommerce.modules.auth.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.neobyte8888.ecommerce.modules.auth.entity.Role;
import com.neobyte8888.ecommerce.modules.auth.entity.User;

/**
 * Lớp chuyển đổi từ Entity User sang UserDetails chuẩn của Spring Security.
 */
public class CustomUserDetails implements UserDetails {

	private UUID id;
	private String email;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;

	// Hàm build để chuyển Entity User -> CustomUserDetails
	public static CustomUserDetails build(User user) {
		// Map các Role của User sang GrantedAuthority của Spring
		List<GrantedAuthority> authorities = new ArrayList<>();

		for (Role role : user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}

		CustomUserDetails customUserDetails = new CustomUserDetails();
		customUserDetails.id = user.getId();
		customUserDetails.email = user.getEmail();
		customUserDetails.password = user.getPassword();
		customUserDetails.authorities = authorities;

		return customUserDetails;
	}

	// Getter
	public UUID getId() {
		return id;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public @Nullable String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email; // Dùng Email làm Username để đăng nhập
	}

	// Các hàm kiểm tra trạng thái tài khoản (Mặc định cho true hết để đơn giản hóa)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
