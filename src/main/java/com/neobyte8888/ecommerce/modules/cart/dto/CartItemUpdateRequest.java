package com.neobyte8888.ecommerce.modules.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartItemUpdateRequest {

	@NotNull(message = "Số lượng không được để trống")
	@Min(value = 0, message = "Số lượng không được âm")
	private Integer quantity;

	public CartItemUpdateRequest() {
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}