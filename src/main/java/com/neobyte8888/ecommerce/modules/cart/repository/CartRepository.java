package com.neobyte8888.ecommerce.modules.cart.repository;

import com.neobyte8888.ecommerce.modules.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
	// Tìm giỏ hàng của một User cụ thể. Dùng Optional vì User có thể chưa từng mua hàng.
	Optional<Cart> findByUserId(UUID userId);
}
