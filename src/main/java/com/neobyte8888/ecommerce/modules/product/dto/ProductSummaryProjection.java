package com.neobyte8888.ecommerce.modules.product.dto;

import java.math.BigDecimal;

// ==========================================
// DTO PROJECTION (TỐI ƯU RAM)
// ==========================================
// Interface này ép Hibernate chỉ SELECT đúng các cột có tên khớp với Getter.
public interface ProductSummaryProjection {
    Long getId();
    String getName();
    String getSlug();      // Frontend luôn cần Slug để làm link bấm vào chi tiết
    BigDecimal getPrice();
    String getImageUrl();  // Khớp với tên thuộc tính imageUrl trong Product Entity
}