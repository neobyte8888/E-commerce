package com.neobyte8888.ecommerce.modules.product.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neobyte8888.ecommerce.common.ApiResponse;
import com.neobyte8888.ecommerce.common.PageResponse;
import com.neobyte8888.ecommerce.exception.BusinessException;
import com.neobyte8888.ecommerce.infrastructure.storage.service.FileStorageService;
import com.neobyte8888.ecommerce.modules.product.dto.ProductRequest;
import com.neobyte8888.ecommerce.modules.product.dto.ProductResponse;
import com.neobyte8888.ecommerce.modules.product.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

	private final ProductService productService;
	private final FileStorageService fileStorageService;
	
	public ProductController(ProductService productService, FileStorageService fileStorageService) {
		this.productService = productService;
		this.fileStorageService = fileStorageService;
	}
	
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
			// Các tham số tìm kiếm (required = false)
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
			// Luôn set defaultValue để nếu Frontend không gửi gì, Server không bị sập (Null Pointer).
			@RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            // Mặc định sắp xếp theo ngày tạo mới nhất
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            // Mặc định giảm dần (mới nhất lên đầu)
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir
	){
		
		PageResponse<ProductResponse> data = productService.getAllProducts(keyword, minPrice, maxPrice, categoryId, page, size, sortBy, sortDir);

		ApiResponse<PageResponse<ProductResponse>> response = new ApiResponse<>(
				HttpStatus.OK.value(),
				"Lấy danh sách sản phẩm thành công",
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
    
    // Dùng @PostMapping với ID sản phẩm để tách biệt dữ liệu text và binary
    // Chốt chặn bảo mật Admin
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> uploadProductImage(
            @PathVariable Long id,
            // Hứng file từ Form Data (key="file")
            @RequestParam("file") MultipartFile file) {
        
        // Bước 1: Gọi dịch vụ để lưu file cục bộ -> Nhận về tên file UUID
        String storedFilename = fileStorageService.storeFile(file);
        
        // Bước 2: Tạo đường dẫn URL ảo (Placeholder). 
        // Sau này ta sẽ có 1 API khác để SERVE static file này (VD: /api/v1/files/{filename})
        // Hoặc khi deploy thật ta sẽ lưu trên S3/Cloudinary và nhận link thật.
        // Tạm thời ta chỉ lưu tên file để DB gọn nhẹ.
        String placeholderUrl = storedFilename; 

        // Bước 3: Cập nhật đường dẫn URL vào Database Sản phẩm
        ProductResponse data = productService.updateProductImage(id, placeholderUrl);

        // Trả về JSON chuẩn ApiResponse (Sprint 3)
        ApiResponse<ProductResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Upload ảnh sản phẩm thành công", 
                data
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> uploadProductGallery(
            @PathVariable Long id,
            // Hứng mảng file từ Form Data (key="files", có chữ s)
            @RequestParam("files") List<MultipartFile> files) {
        
        // 1. TƯ DUY SENIOR: Validate chặn Spam
        if (files == null || files.isEmpty()) {
            throw new BusinessException("Vui lòng chọn ít nhất 1 ảnh để upload.");
        }
        if (files.size() > 5) { // Giới hạn số lượng ảnh upload trong 1 lần để chống DDOS ổ cứng
            throw new BusinessException("Chỉ được phép upload tối đa 5 ảnh cùng lúc.");
        }

        // 2. Lưu từng file ra ổ cứng (Vòng lặp này nằm NGOÀI Database Transaction)
        List<String> uploadedFilenames = new ArrayList<>();
        for (MultipartFile file : files) {
            String storedFilename = fileStorageService.storeFile(file); // Tái sử dụng hàm đã viết ở Sprint 17 (Phần 2)
            uploadedFilenames.add(storedFilename);
        }

        // 3. Gửi danh sách tên file UUID xuống DB để lưu lại
        ProductResponse data = productService.uploadProductGallery(id, uploadedFilenames);

        // 4. Trả kết quả
        ApiResponse<ProductResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Upload bộ sưu tập ảnh thành công", 
                data
        );
        
        return ResponseEntity.ok(response);
    }
}
