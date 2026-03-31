package com.neobyte8888.ecommerce.modules.product.service;

import java.util.List;

import com.neobyte8888.ecommerce.common.PageResponse;
import com.neobyte8888.ecommerce.modules.product.dto.ProductRequest;
import com.neobyte8888.ecommerce.modules.product.dto.ProductResponse;

public interface ProductService {
	ProductResponse createProduct(ProductRequest productRequest);
	ProductResponse updateProduct(Long id, ProductRequest request);
	void deleteProduct(Long id);
	ProductResponse getProductById(Long id);
	List<ProductResponse> getAllProducts();
	
}
