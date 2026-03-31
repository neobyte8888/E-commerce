package com.neobyte8888.ecommerce.modules.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neobyte8888.ecommerce.common.ApiResponse;
import com.neobyte8888.ecommerce.modules.product.dto.ProductRequest;
import com.neobyte8888.ecommerce.modules.product.dto.ProductResponse;
import com.neobyte8888.ecommerce.modules.product.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

	private final ProductService productService;
	
	public ProductController(ProductService productService) {
		this.productService = productService;
	}
	
	@GetMapping
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(){
		List<ProductResponse> data = productService.getAllProducts();

		ApiResponse<List<ProductResponse>> response = new ApiResponse<List<ProductResponse>>(
				HttpStatus.OK.value(),
				"Lấy chi tiết sản phẩm thành công",
				data
		);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id){
		ProductResponse data = productService.getProductById(id);
		ApiResponse<ProductResponse> response = new ApiResponse<ProductResponse>(
				HttpStatus.OK.value(),
				"Lấy chi tiết sản phẩm thành công",
				data
		);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request){
		ProductResponse data = productService.createProduct(request);
		ApiResponse<ProductResponse> response = new ApiResponse<ProductResponse>(
				HttpStatus.CREATED.value(),
				"Tạo sản phẩm thành công",
				data
		);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        ProductResponse data = productService.updateProduct(id, request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật sản phẩm thành công", data));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Xóa sản phẩm thành công"));
    }
}
