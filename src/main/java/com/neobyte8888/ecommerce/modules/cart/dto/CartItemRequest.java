package com.neobyte8888.ecommerce.modules.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartItemRequest {

	@NotNull(message = "ID Sản phẩm không được để trống")
	private Long productId;

	// Validate ngay từ cửa, không cho phép gửi số lượng âm hoặc bằng 0
	@NotNull(message = "Số lượng không được để trống")
	@Min(value = 1, message = "Số lượng phải lớn hơn 0")
	private Integer quantity;

	public CartItemRequest() {
	}

	// Getters & Setters
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}