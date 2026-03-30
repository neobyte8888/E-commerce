package com.neobyte8888.ecommerce.modules.product.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.neobyte8888.ecommerce.common.ApiResponse;
import com.neobyte8888.ecommerce.modules.product.dto.CategoryRequest;
import com.neobyte8888.ecommerce.modules.product.dto.CategoryResponse;
import com.neobyte8888.ecommerce.modules.product.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // API Lấy danh sách: Ai cũng xem được (kể cả chưa đăng nhập nếu ta mở CORS/PermitAll)
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> data = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy danh sách danh mục thành công", data));
    }

    // Dùng @PreAuthorize để CHẶN đứng mọi user thường. 
    // Chỉ có người sở hữu token chứa ROLE_ADMIN mới lọt vào được hàm này.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse data = categoryService.createCategory(request);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.CREATED.value(), "Tạo danh mục thành công", data), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse data = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật danh mục thành công", data));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Xóa danh mục thành công"));
    }
}