package com.neobyte8888.ecommerce.modules.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neobyte8888.ecommerce.modules.product.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
	// Kiểm tra xem tên danh mục hoặc slug đã tồn tại chưa (tránh trùng lặp)
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
