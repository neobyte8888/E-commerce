package com.neobyte8888.ecommerce.modules.product.repository;

import com.neobyte8888.ecommerce.modules.product.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    // Dùng Criteria API để ráp query linh hoạt
    public static Specification<Product> filterProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice, Long categoryId) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo tên sản phẩm (Tìm kiếm tương đối - LIKE %keyword%)
            if (keyword != null && !keyword.trim().isEmpty()) {
                // criteriaBuilder.lower giúp tìm kiếm không phân biệt chữ hoa/thường (Case-insensitive)
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), 
                        "%" + keyword.toLowerCase() + "%"
                ));
            }

            // 2. Lọc theo khoảng giá: Lớn hơn hoặc bằng minPrice
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            // 3. Lọc theo khoảng giá: Nhỏ hơn hoặc bằng maxPrice
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            // 4. Lọc theo danh mục
            if (categoryId != null) {
                // root.get("category").get("id") tương đương với câu SQL: product.category_id = ?
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            // Gộp tất cả các điều kiện lại bằng toán tử AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}