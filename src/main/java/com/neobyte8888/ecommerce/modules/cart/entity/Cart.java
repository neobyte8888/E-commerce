package com.neobyte8888.ecommerce.modules.cart.entity;

import java.util.ArrayList;
import java.util.List;

import com.neobyte8888.ecommerce.common.BaseEntity;
import com.neobyte8888.ecommerce.modules.auth.entity.User;

import jakarta.persistence.*;

@Entity
@Table(name = "carts")
public class Cart extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Quan hệ 1-1 với User
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	// Quan hệ 1-Nhiều với CartItem.
	// orphanRemoval = true: Khi ta xóa 1 item khỏi list, Hibernate tự động xóa dòng đó dưới DB.
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> items = new ArrayList<>();

	public Cart() {
	}

	public Cart(User user) {
		this.user = user;
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

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	// Hàm phụ trợ (Helper method) để thêm/sửa item cho chuẩn quan hệ 2 chiều
	public void addItem(CartItem item) {
		items.add(item);
		item.setCart(this);
	}

	public void removeItem(CartItem item) {
		items.remove(item);
		item.setCart(null);
	}

}
