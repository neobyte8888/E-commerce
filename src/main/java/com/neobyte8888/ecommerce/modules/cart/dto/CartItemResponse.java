package com.neobyte8888.ecommerce.modules.cart.dto;

import java.math.BigDecimal;

public class CartItemResponse {
	private Long id; // ID của dòng CartItem (Dùng để Frontend gọi API Xóa item)

	// Thông tin cơ bản của Sản phẩm (Đồng bộ Real-time từ bảng Product)
	private Long productId;
	private String productName;
	private String productSlug;
	private String imageUrl;

	// Giá tiền này là giá MỚI NHẤT được lấy lên từ bảng Product tại thời điểm xem
	// giỏ hàng
	private BigDecimal price;

	private Integer quantity;

	// Tổng tiền của riêng món này = price * quantity (Tính toán sẵn ở Backend để
	// Frontend đỡ phải tính)
	private BigDecimal subTotal;

	// Cờ cảnh báo biến động tồn kho
	private Boolean isStockAltered;

	public CartItemResponse() {
	}

	// Getters & Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getProductSlug() {
		return productSlug;
	}

	public void setProductSlug(String productSlug) {
		this.productSlug = productSlug;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public Boolean getIsStockAltered() {
		return isStockAltered;
	}

	public void setIsStockAltered(Boolean isStockAltered) {
		this.isStockAltered = isStockAltered;
	}
}