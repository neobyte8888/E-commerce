package com.neobyte8888.ecommerce.modules.product.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.neobyte8888.ecommerce.common.BaseEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
//Biến lệnh DELETE vật lý thành lệnh UPDATE is_deleted = true
@SQLDelete(sql = "UPDATE categories SET is_deleted = true WHERE id=?")
//Mọi câu lệnh SELECT từ nay về sau (findAll, findById...) 
//Hibernate sẽ TỰ ĐỘNG nhét thêm chữ "AND is_deleted = false" vào câu SQL.
@SQLRestriction("is_deleted = false")
public class Category extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String slug;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Category parent;

	// Danh sách các danh mục con (Không bắt buộc phải có, nhưng rất tiện để query
	// lấy menu cây)
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private List<Category> subCategories = new ArrayList<>();

	// Danh sách sản phẩm thuộc danh mục này
	@OneToMany(mappedBy = "category")
	private List<Product> products = new ArrayList<>();
	
	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	// Constructors
	public Category() {
	}

	public Category(String name, String slug) {
		this.name = name;
		this.slug = slug;
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

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public List<Category> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<Category> subCategories) {
		this.subCategories = subCategories;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	public Boolean getDeleted() {
		return isDeleted;
	}

	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
}
