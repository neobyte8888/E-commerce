package com.neobyte8888.ecommerce.modules.order.dto;

import com.neobyte8888.ecommerce.modules.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderStatusUpdateRequest {

	@NotNull(message = "Trạng thái mới không được để trống")
	private OrderStatus status;

	public OrderStatusUpdateRequest() {
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}
}