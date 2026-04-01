package com.neobyte8888.ecommerce.modules.product.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neobyte8888.ecommerce.common.PageResponse;
import com.neobyte8888.ecommerce.exception.BusinessException;
import com.neobyte8888.ecommerce.exception.ResourceNotFoundException;
import com.neobyte8888.ecommerce.modules.product.dto.ProductRequest;
import com.neobyte8888.ecommerce.modules.product.dto.ProductResponse;
import com.neobyte8888.ecommerce.modules.product.entity.Category;
import com.neobyte8888.ecommerce.modules.product.entity.Product;
import com.neobyte8888.ecommerce.modules.product.entity.ProductImage;
import com.neobyte8888.ecommerce.modules.product.repository.CategoryRepository;
import com.neobyte8888.ecommerce.modules.product.repository.ProductRepository;
import com.neobyte8888.ecommerce.modules.product.repository.ProductSpecification;
import com.neobyte8888.ecommerce.modules.product.service.ProductService;
import com.neobyte8888.ecommerce.util.SlugUtils;

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
    public PageResponse<ProductResponse> getAllProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice, Long categoryId,
    		int pageNo, int pageSize, String sortBy, String sortDir) {
    	// Nhờ @SQLRestriction, hàm findAll tự động ẩn các sản phẩm đã bị xóa mềm (is_deleted = true)
    	
    	// 1. Xác định hướng sắp xếp (ASC tăng dần, DESC giảm dần)
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        
        // 2. Tạo đối tượng Pageable (Spring bắt đầu đếm trang từ 0)
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        
        // 3. Gọi Nhà máy ráp SQL động
        Specification<Product> spec = ProductSpecification.filterProducts(keyword, minPrice, maxPrice, categoryId);
        
        // 4. Query Database: Hibernate sẽ tự lo LIMIT, OFFSET và SELECT COUNT(*)
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        
        // 5. Chuyển đổi từ List<Product> sang List<ProductResponse>
        List<ProductResponse> content = productPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        // 6. Đóng gói vào "Chiếc hộp chuẩn" PageResponse
        PageResponse<ProductResponse> response = new PageResponse<ProductResponse>();
        response.setContent(content);
        response.setPageNo(productPage.getNumber());
        response.setPageSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());
        
        return response;
    }
    
    @Override
    @Transactional
    public ProductResponse updateProductImage(Long id, String imageUrl) {
        // Tìm sản phẩm
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Sản phẩm với ID: " + id));

        // Cập nhật đường dẫn ảnh
        product.setImageUrl(imageUrl);
        
        // Lưu lại DB và trả về DTO
        return mapToResponse(productRepository.save(product));
    }
    
    @Override
    @Transactional
    public ProductResponse uploadProductGallery(Long id, List<String> imageUrls) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Sản phẩm với ID: " + id));

        // Biến URL thành Entity ProductImage và nhét vào bộ sưu tập của Product
        for (String url : imageUrls) {
            ProductImage productImage = new ProductImage(product, url);
            product.getGalleryImages().add(productImage);
        }

        // Lưu lại DB và map ra DTO
        return mapToResponse(productRepository.save(product));
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
        response.setImageUrl(product.getImageUrl());
        
        // Duyệt qua List<ProductImage> lấy ra List<String>
        if (product.getGalleryImages() != null && !product.getGalleryImages().isEmpty()) {
            List<String> galleryUrls = product.getGalleryImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());
            response.setGallery(galleryUrls);
        }
        
        // Trả về thêm thông tin danh mục để Frontend làm UI (VD: Hiển thị "Thuộc danh mục: Điện thoại")
        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }
        return response;
    }

}
