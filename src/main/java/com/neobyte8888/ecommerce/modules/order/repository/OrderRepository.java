package com.neobyte8888.ecommerce.modules.order.repository;

import com.neobyte8888.ecommerce.modules.order.entity.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	// Phân trang danh sách đơn hàng của 1 User
	@EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    Page<Order> findByUserId(UUID userId, Pageable pageable);

    // CHỐNG IDOR: Tìm đơn hàng theo ID VÀ User ID. 
    // Nếu Hacker truyền Order ID của người khác, hàm này sẽ trả về rỗng (Optional.empty)
	@EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    Optional<Order> findByIdAndUserId(Long id, UUID userId);
}