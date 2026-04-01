package com.neobyte8888.ecommerce.modules.cart.entity;

import com.neobyte8888.ecommerce.common.BaseEntity;
import com.neobyte8888.ecommerce.modules.product.entity.Product;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id", nullable = false)
	private Cart cart;

	// Chỉ trỏ ID lấy thông tin, KHÔNG lưu giá tiền ở đây
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false)
	private Integer quantity;

	public CartItem() {
	}

	public CartItem(Cart cart, Product product, Integer quantity) {
		this.cart = cart;
		this.product = product;
		this.quantity = quantity;
	}

	// Getters & Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}