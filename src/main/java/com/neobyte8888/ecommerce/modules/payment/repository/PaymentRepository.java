package com.neobyte8888.ecommerce.modules.payment.repository;

import com.neobyte8888.ecommerce.modules.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    // Tìm giao dịch thanh toán dựa trên ID của Đơn hàng (Dùng để kiểm tra xem đơn này đã tạo thanh toán chưa)
    Optional<Payment> findByOrderId(Long orderId);
}