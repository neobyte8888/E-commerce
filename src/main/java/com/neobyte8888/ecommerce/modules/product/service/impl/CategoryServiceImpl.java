package com.neobyte8888.ecommerce.modules.product.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neobyte8888.ecommerce.exception.BusinessException;
import com.neobyte8888.ecommerce.exception.ResourceNotFoundException;
import com.neobyte8888.ecommerce.modules.product.dto.CategoryRequest;
import com.neobyte8888.ecommerce.modules.product.dto.CategoryResponse;
import com.neobyte8888.ecommerce.modules.product.entity.Category;
import com.neobyte8888.ecommerce.modules.product.repository.CategoryRepository;
import com.neobyte8888.ecommerce.modules.product.repository.ProductRepository;
import com.neobyte8888.ecommerce.modules.product.service.CategoryService;
import com.neobyte8888.ecommerce.util.SlugUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository; // Dùng để check sản phẩm mồ côi

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BusinessException("Tên danh mục đã tồn tại!");
        }
        
        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(SlugUtils.toSlug(request.getName()));

        // Nếu có truyền parentId, kiểm tra xem danh mục cha có tồn tại không
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha với ID: " + request.getParentId()));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        // Nếu đổi tên, phải check xem tên mới có bị trùng với danh mục khác không
        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new BusinessException("Tên danh mục đã tồn tại!");
        }

        category.setName(request.getName());
        category.setSlug(SlugUtils.toSlug(request.getName()));

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BusinessException("Danh mục không thể tự làm cha của chính nó!");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        // 1. Không cho xóa nếu đang có danh mục con
        if (!category.getSubCategories().isEmpty()) {
            throw new BusinessException("Không thể xóa danh mục này vì đang chứa " + category.getSubCategories().size() + " danh mục con!");
        }

        // 2. Không cho xóa nếu đang chứa sản phẩm
        if (productRepository.existsByCategoryId(id)) {
            throw new BusinessException("Không thể xóa danh mục này vì đang chứa sản phẩm! Vui lòng chuyển sản phẩm sang danh mục khác trước.");
        }

        // 3. XÓA MỀM (Soft Delete): Chỉ đổi cờ thành true, KHÔNG GỌI repository.delete()
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        // Nhờ @SQLRestriction("is_deleted = false") ở Entity, hàm findAll() sẽ tự động ẩn các danh mục đã xóa mềm
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Hàm phụ trợ map Entity sang DTO (Thay thế MapStruct/Lombok)
    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        if (category.getParent() != null) {
            response.setParentId(category.getParent().getId());
        }
        return response;
    }
}