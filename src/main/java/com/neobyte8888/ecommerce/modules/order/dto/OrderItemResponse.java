package com.neobyte8888.ecommerce.modules.order.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
	private Long productId;
	private String productName;
	private String imageUrl;
	private Integer quantity;

	// Giá tiền LÚC MUA (Lịch sử bất biến)
	private BigDecimal price;
	private BigDecimal subTotal;

	public OrderItemResponse() {
	}

	// Getters & Setters
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}
}