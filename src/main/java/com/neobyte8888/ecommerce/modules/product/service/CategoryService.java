package com.neobyte8888.ecommerce.modules.product.service;

import java.util.List;

import com.neobyte8888.ecommerce.modules.product.dto.CategoryRequest;
import com.neobyte8888.ecommerce.modules.product.dto.CategoryResponse;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    List<CategoryResponse> getAllCategories();
}