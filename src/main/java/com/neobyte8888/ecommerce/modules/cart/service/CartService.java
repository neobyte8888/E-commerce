package com.neobyte8888.ecommerce.modules.cart.service;

import com.neobyte8888.ecommerce.modules.cart.dto.CartItemRequest;
import com.neobyte8888.ecommerce.modules.cart.dto.CartResponse;

public interface CartService {

	// Truyền vào email (từ SecurityContext) để đảm bảo bảo mật, user không thể truyền userId giả mạo qua API.
	CartResponse addToCart(CartItemRequest request, String userEmail);
	
	CartResponse getMyCart(String userEmail);
    CartResponse removeCartItem(Long itemId, String userEmail);
    
    CartResponse updateCartItemQuantity(Long itemId, Integer quantity, String userEmail);
    
    CartResponse clearCart(String userEmail);
}
