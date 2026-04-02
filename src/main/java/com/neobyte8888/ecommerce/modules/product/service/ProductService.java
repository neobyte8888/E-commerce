package com.neobyte8888.ecommerce.modules.product.service;

import java.math.BigDecimal;
import java.util.List;

import com.neobyte8888.ecommerce.common.PageResponse;
import com.neobyte8888.ecommerce.modules.product.dto.ProductRequest;
import com.neobyte8888.ecommerce.modules.product.dto.ProductResponse;
import com.neobyte8888.ecommerce.modules.product.dto.ProductSummaryProjection;

public interface ProductService {
	ProductResponse createProduct(ProductRequest productRequest);
	ProductResponse updateProduct(Long id, ProductRequest request);
	void deleteProduct(Long id);
	ProductResponse getProductById(Long id);
	PageResponse<ProductResponse> getAllProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice, Long categoryId, 
			int pageNo, int pageSize, String sortBy, String sortDir);
	// Cập nhật đường dẫn ảnh của sản phẩm
    ProductResponse updateProductImage(Long id, String imageUrl);
    
    // Lưu danh sách ảnh vào bộ sưu tập (Gallery)
    ProductResponse uploadProductGallery(Long id, List<String> imageUrls);
    
    PageResponse<ProductSummaryProjection> getProductsForHomePage(int pageNo, int pageSize, String sortBy, String sortDir);
	
}
