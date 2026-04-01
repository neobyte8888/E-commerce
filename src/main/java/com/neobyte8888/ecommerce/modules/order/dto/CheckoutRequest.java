package com.neobyte8888.ecommerce.modules.order.dto;

import com.neobyte8888.ecommerce.modules.order.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

	@NotBlank(message = "Địa chỉ giao hàng không được để trống")
	private String shippingAddress;

	@NotNull(message = "Phương thức thanh toán không được để trống")
	private PaymentMethod paymentMethod;

	public CheckoutRequest() {
	}

	// Getters & Setters
	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
}