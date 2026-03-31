package com.neobyte8888.ecommerce.modules.product.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neobyte8888.ecommerce.common.SlugUtils;
import com.neobyte8888.ecommerce.exception.BusinessException;
import com.neobyte8888.ecommerce.exception.ResourceNotFoundException;
import com.neobyte8888.ecommerce.modules.product.dto.ProductRequest;
import com.neobyte8888.ecommerce.modules.product.dto.ProductResponse;
import com.neobyte8888.ecommerce.modules.product.entity.Category;
import com.neobyte8888.ecommerce.modules.product.entity.Product;
import com.neobyte8888.ecommerce.modules.product.repository.CategoryRepository;
import com.neobyte8888.ecommerce.modules.product.repository.ProductRepository;
import com.neobyte8888.ecommerce.modules.product.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	
	public ProductServiceImpl(ProductRepository productRepository,
							CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}
	
	@Override
	@Transactional
	public ProductResponse createProduct(ProductRequest productRequest) {
		// 1. Kiểm tra trùng tên (chống lỗi duplicate slug)
		if(productRepository.existsByName(productRequest.getName())) {
			throw new BusinessException("Tên sản phẩm đã tồn tại trong hệ thống");
		}
		
		// 2. Kiểm tra ID danh mục có tồn tại hay không
		Category category = categoryRepository.findById(productRequest.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Danh mục với ID: " + productRequest.getCategoryId()));
	
		// 3. Khởi tạo và gán giá trị
		Product product = new Product();
		product.setName(productRequest.getName());
		product.setSlug(SlugUtils.toSlug(productRequest.getName()));
		product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setCategory(category);
        
        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
        
	}

	@Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Sản phẩm với ID: " + id));

        // Kiểm tra trùng tên nếu Admin đổi tên sản phẩm
        if (!product.getName().equals(request.getName()) && productRepository.existsByName(request.getName())) {
            throw new BusinessException("Tên sản phẩm đã tồn tại!");
        }

        // Cập nhật Danh mục nếu có thay đổi
        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Danh mục với ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        product.setName(request.getName());
        product.setSlug(SlugUtils.toSlug(request.getName()));
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        return mapToResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Sản phẩm với ID: " + id));

        // XÓA MỀM (Soft Delete)
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Sản phẩm với ID: " + id));
        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        // Nhờ @SQLRestriction, hàm findAll tự động ẩn các sản phẩm đã bị xóa mềm (is_deleted = true)
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
	
	// Hàm phụ trợ map Entity sang DTO
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSlug(product.getSlug());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        
        // Trả về thêm thông tin danh mục để Frontend làm UI (VD: Hiển thị "Thuộc danh mục: Điện thoại")
        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }
        return response;
    }

}
