package com.neobyte8888.ecommerce.modules.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Bước 1: Lấy token từ header
            String jwt = getJwtFromRequest(request);

            // Bước 2: Nếu có thẻ và thẻ hợp lệ (Không hết hạn, không bị sửa đổi - gọi sang Sprint 9)
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                
                // Bước 3: Lấy UUID từ thẻ
                UUID userId = tokenProvider.getUserIdFromJWT(jwt);

                // Bước 4: Load thông tin User từ Database
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);

                // Bước 5: Tạo giấy thông hành nội bộ của Spring Security
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                // Gắn thêm chi tiết về Request (IP, SessionId...)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Bước 6: Lập tức nhét thẻ này vào SecurityContext (Bộ nhớ luồng hiện tại)
                // Từ giờ phút này, Spring Security chính thức ghi nhận: "Thanh niên này đã đăng nhập và có quyền X"
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Cố tình nuốt lỗi ở đây, vì nếu token sai/hết hạn, SecurityContextHolder sẽ bị rỗng (null).
            // Khi đó request đi tiếp sẽ bị Spring Security chặn lại và ném thẳng sang JwtAuthenticationEntryPoint (bước 2).
            logger.error("Không thể thiết lập xác thực người dùng trong Security Context", ex);
        }

        // Bước 7: Mở cổng cho request đi tiếp vào trạm gác tiếp theo hoặc vào Controller
        filterChain.doFilter(request, response);
    }

    // Hàm phụ trợ: Bóc tách chuỗi token (Cắt bỏ chữ "Bearer ")
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}