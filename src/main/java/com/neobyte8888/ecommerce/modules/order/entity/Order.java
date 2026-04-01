package com.neobyte8888.ecommerce.modules.order.entity;

import com.neobyte8888.ecommerce.common.BaseEntity;
import com.neobyte8888.ecommerce.modules.auth.entity.User;
import com.neobyte8888.ecommerce.modules.order.enums.OrderStatus;
import com.neobyte8888.ecommerce.modules.order.enums.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal totalAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private OrderStatus status;

	@Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
	private String shippingAddress;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false, length = 50)
	private PaymentMethod paymentMethod;

	// Quan hệ 1-N với OrderItem
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems = new ArrayList<>();

	public Order() {
	}

	// Getters & Setters (KHÔNG LOMBOK)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

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

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	// Helper method
	public void addOrderItem(OrderItem item) {
		orderItems.add(item);
		item.setOrder(this);
	}
}