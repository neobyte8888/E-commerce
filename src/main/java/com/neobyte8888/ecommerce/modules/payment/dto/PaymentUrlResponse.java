package com.neobyte8888.ecommerce.modules.payment.dto;

public class PaymentUrlResponse {
	private String paymentUrl;

	public PaymentUrlResponse() {
	}

	public PaymentUrlResponse(String paymentUrl) {
		this.paymentUrl = paymentUrl;
	}

	public String getPaymentUrl() {
		return paymentUrl;
	}

	public void setPaymentUrl(String paymentUrl) {
		this.paymentUrl = paymentUrl;
	}
}