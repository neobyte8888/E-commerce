package com.neobyte8888.ecommerce.modules.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.neobyte8888.ecommerce.modules.product.dto.ProductSummaryProjection;
import com.neobyte8888.ecommerce.modules.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>{

	// Vũ khí tối thượng để check xem Danh mục có chứa Sản phẩm nào không
    // trước khi cho phép xóa (Chống Orphan data).
    boolean existsByCategoryId(Long categoryId);
    
    // Chống trùng tên sản phảm
    boolean existsByName(String name);
    
    // ==========================================
    // TRUY VẤN TỐI ƯU CHO TRANG CHỦ
    // ==========================================
    // Spring Data JPA sẽ phân tích kiểu trả về (ProductSummaryProjection) 
    // và TỰ ĐỘNG sinh ra câu lệnh: SELECT id, name, slug, price, image_url FROM products
    Page<ProductSummaryProjection> findAllProjectedBy(Pageable pageable);
    
    // Nếu bạn muốn lọc theo Category hiển thị trên trang chủ:
    Page<ProductSummaryProjection> findByCategoryId(Long categoryId, Pageable pageable);
}
