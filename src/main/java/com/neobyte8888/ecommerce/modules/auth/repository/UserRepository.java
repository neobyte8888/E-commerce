package com.neobyte8888.ecommerce.modules.auth.repository;

import org.springframework.stereotype.Repository;
import com.neobyte8888.ecommerce.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Spring Data JPA sẽ tự động dịch tên hàm này thành câu lệnh SQL:
    // SELECT count(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);
    
    Optional<User> findByEmail(String email);
}