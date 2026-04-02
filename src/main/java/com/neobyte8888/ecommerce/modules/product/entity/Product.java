package com.neobyte8888.ecommerce.modules.product.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.neobyte8888.ecommerce.common.BaseEntity;

@Entity
@Table(name = "products")
//Mọi câu lệnh SELECT từ nay về sau (findAll, findById...) 
//Hibernate sẽ TỰ ĐỘNG nhét thêm chữ "AND is_deleted = false" vào câu SQL.
@SQLRestriction("is_deleted = false")
public class Product extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String slug;

	@Column(columnDefinition = "TEXT")
	private String description;

	// Map chính xác với NUMERIC(19,2) dưới database bằng BigDecimal
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal price;

	@Column(nullable = false)
	private Integer stock = 0;

	// Nhiều Sản phẩm thuộc về 1 Danh mục
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	// Lưu đường dẫn ảnh
	@Column(name = "image_url", length = 500)
	private String imageUrl;

	// Danh sách ảnh chi tiết (Gallery)
	// cascade = CascadeType.ALL và orphanRemoval = true
	// Giúp Spring JPA tự động xóa/thêm ảnh con khi ta thao tác trên list này.
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductImage> galleryImages = new ArrayList<>();

	// KHÓA LẠC QUAN (OPTIMISTIC LOCKING)
	@Version
	@Column(nullable = false)
	private Long version;

	// Constructors
	public Product() {
	}

	public Product(String name, String slug, BigDecimal price, Integer stock, Category category) {
		this.name = name;
		this.slug = slug;
		this.price = price;
		this.stock = stock;
		this.category = category;
	}

	// Getters & Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Boolean getDeleted() {
		return isDeleted;
	}

	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public List<ProductImage> getGalleryImages() {
		return galleryImages;
	}

	public void setGalleryImages(List<ProductImage> galleryImages) {
		this.galleryImages = galleryImages;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}