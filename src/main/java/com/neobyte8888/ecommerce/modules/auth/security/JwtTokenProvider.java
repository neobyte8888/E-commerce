package com.neobyte8888.ecommerce.modules.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.neobyte8888.ecommerce.exception.InvalidCredentialsException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

	// Lấy config từ application.yml
	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration}")
	private long jwtExpirationMs;

	/**
	 * Tạo khóa ký bí mật từ chuỗi base64. Cú pháp mới của JJWT yêu cầu dùng
	 * SecretKey thay vì String.
	 */
	private SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * Hàm sinh thẻ Token sau khi User đăng nhập thành công.
	 */
	public String generateToken(Authentication authentication) {
		// Ép kiểu principal về CustomUserDetails mà ta vừa tạo ở trên
		CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

		// Build JWT (Payload chứa Subject là UUID của User)
		return Jwts.builder().subject(userPrincipal.getId().toString())
				// Có thể thêm custom claims như Role vào đây nếu muốn giảm tải DB
				.issuedAt(now).expiration(expiryDate).signWith(getSigningKey()) // Ký bằng thuật toán HMAC-SHA mạnh nhất
				.compact();
	}

	/**
	 * Lấy UUID của User từ Token (Dùng khi filter chặn request)
	 */
	public UUID getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();

		return UUID.fromString(claims.getSubject());
	}

	/**
	 * Xác minh thẻ Token có hợp lệ, bị fake, hay hết hạn không.
	 */
	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
			return true;
		} catch (SignatureException ex) {
			// Chữ ký không khớp -> Hacker đã sửa đổi payload (Ví dụ: sửa id từ 1 sang 2)
			throw new InvalidCredentialsException("Chữ ký JWT không hợp lệ");
		} catch (MalformedJwtException ex) {
			// Chuỗi JWT bị sai định dạng
			throw new InvalidCredentialsException("Định dạng JWT không hợp lệ");
		} catch (ExpiredJwtException ex) {
			// Token đã quá 24h
			throw new InvalidCredentialsException("Token đã hết hạn, vui lòng đăng nhập lại");
		} catch (UnsupportedJwtException ex) {
			throw new InvalidCredentialsException("JWT không được hệ thống hỗ trợ");
		} catch (IllegalArgumentException ex) {
			throw new InvalidCredentialsException("Chuỗi JWT bị trống");
		}
	}
}