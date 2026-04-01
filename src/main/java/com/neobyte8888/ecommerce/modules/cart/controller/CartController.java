package com.neobyte8888.ecommerce.modules.cart.controller;

import com.neobyte8888.ecommerce.common.ApiResponse;
import com.neobyte8888.ecommerce.modules.cart.dto.CartItemRequest;
import com.neobyte8888.ecommerce.modules.cart.dto.CartItemUpdateRequest;
import com.neobyte8888.ecommerce.modules.cart.dto.CartResponse;
import com.neobyte8888.ecommerce.modules.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Chặn ngay vòng ngoài, chỉ USER và ADMIN đã đăng nhập mới được xài giỏ hàng
    @PostMapping("/items")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@Valid @RequestBody CartItemRequest request) {
        
        // BƯỚC 1 (Controller): Trích xuất Email (User identifier) từ SecurityContext (Token)
        // Spring Security (Sprint 10) đã tự động parse Token và nhét thông tin vào đây.
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Gọi Service xử lý
        CartResponse data = cartService.addToCart(request, userEmail);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Thêm vào giỏ hàng thành công", 
                data
        ));
    }
    
    // ==========================================
    // API LẤY GIỎ HÀNG (REAL-TIME)
    // ==========================================
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        CartResponse data = cartService.getMyCart(userEmail);
        
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Lấy thông tin giỏ hàng thành công", 
                data
        ));
    }
    
    // ==========================================
    // API XÓA MỘT MÓN KHỎI GIỎ HÀNG
    // ==========================================
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(@PathVariable Long itemId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        // Trả về luôn Giỏ hàng MỚI SAU KHI XÓA để Frontend cập nhật lại giao diện (đỡ phải gọi lại API GET)
        CartResponse data = cartService.removeCartItem(itemId, userEmail);
        
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Xóa sản phẩm khỏi giỏ hàng thành công", 
                data
        ));
    }
    
    // ==========================================
    // SPRINT 21: API CẬP NHẬT SỐ LƯỢNG ITEM
    // ==========================================
    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @PathVariable Long itemId, 
            @Valid @RequestBody CartItemUpdateRequest request) {
        
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        CartResponse data = cartService.updateCartItemQuantity(itemId, request.getQuantity(), userEmail);
        
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Cập nhật giỏ hàng thành công", 
                data
        ));
    }

    // ==========================================
    // SPRINT 22: API DỌN SẠCH GIỎ HÀNG
    // ==========================================
    @DeleteMapping("/clear")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart() {
        
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        CartResponse data = cartService.clearCart(userEmail);
        
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Đã dọn sạch giỏ hàng", 
                data
        ));
    }
}