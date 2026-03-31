package com.neobyte8888.ecommerce.modules.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProductRequest {

	@NotBlank(message = "Tên sản phẩm không được để trống")
	private String name;

	private String description;

	// TƯ DUY SENIOR: Dùng @DecimalMin thay vì @Min cho kiểu BigDecimal
	@NotNull(message = "Giá sản phẩm không được để trống")
	@DecimalMin(value = "0.0", inclusive = true, message = "Giá sản phẩm không được nhỏ hơn 0")
	private BigDecimal price;

	@NotNull(message = "Số lượng tồn kho không được để trống")
	@Min(value = 0, message = "Tồn kho không được nhỏ hơn 0")
	private Integer stock;

	@NotNull(message = "ID Danh mục không được để trống")
	private Long categoryId;

	public ProductRequest() {
	}

	// Getters & Setters (KHÔNG LOMBOK)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
}
