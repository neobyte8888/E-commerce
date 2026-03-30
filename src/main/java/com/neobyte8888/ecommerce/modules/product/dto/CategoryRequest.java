package com.neobyte8888.ecommerce.modules.product.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequest {

	@NotBlank(message = "Tên danh mục không được để trống")
	private String name;

	// Nếu tạo danh mục con thì truyền parentId, nếu danh mục gốc thì để null
	private Long parentId;

	public CategoryRequest() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
}