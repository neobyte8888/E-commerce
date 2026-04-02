package com.neobyte8888.ecommerce.modules.payment.entity;

import com.neobyte8888.ecommerce.common.BaseEntity;
import com.neobyte8888.ecommerce.modules.order.entity.Order;
import com.neobyte8888.ecommerce.modules.payment.enums.PaymentProvider;
import com.neobyte8888.ecommerce.modules.payment.enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	// Quan hệ 1-1 với Order (Do đã đánh UNIQUE index dưới Database)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false, unique = true)
	private Order order;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private PaymentProvider provider;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private PaymentStatus status;

	@Column(name = "provider_transaction_id", length = 255)
	private String providerTransactionId;

	public Payment() {
	}

	// Getters & Setters
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public PaymentProvider getProvider() {
		return provider;
	}

	public void setProvider(PaymentProvider provider) {
		this.provider = provider;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public String getProviderTransactionId() {
		return providerTransactionId;
	}

	public void setProviderTransactionId(String providerTransactionId) {
		this.providerTransactionId = providerTransactionId;
	}
}