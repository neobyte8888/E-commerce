package com.neobyte8888.ecommerce.modules.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neobyte8888.ecommerce.modules.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

	// Vũ khí tối thượng để check xem Danh mục có chứa Sản phẩm nào không
    // trước khi cho phép xóa (Chống Orphan data).
    boolean existsByCategoryId(Long categoryId);
}
