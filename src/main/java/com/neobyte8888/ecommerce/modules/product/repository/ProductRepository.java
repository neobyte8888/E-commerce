package com.neobyte8888.ecommerce.modules.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.neobyte8888.ecommerce.modules.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>{

	// Vũ khí tối thượng để check xem Danh mục có chứa Sản phẩm nào không
    // trước khi cho phép xóa (Chống Orphan data).
    boolean existsByCategoryId(Long categoryId);
    
    // Chống trùng tên sản phảm
    boolean existsByName(String name);
}
