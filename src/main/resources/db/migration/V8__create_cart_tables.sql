-- 1. Bảng Carts: Mỗi User chỉ có đúng 1 giỏ hàng (Quan hệ 1-1)
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
	updated_by VARCHAR(255)
);

-- 2. Bảng Cart_Items: Chi tiết từng món hàng trong giỏ (Quan hệ 1-Nhiều với Carts)
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    
    -- Ràng buộc số lượng luôn phải lớn hơn 0 ở cấp độ Database
    quantity INT NOT NULL CHECK (quantity > 0),
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
	updated_by VARCHAR(255),
    
    -- Chống trùng lặp. 
    -- Nếu user thêm cùng 1 sản phẩm 2 lần, ta chỉ UPDATE quantity chứ không INSERT dòng mới.
    CONSTRAINT uk_cart_product UNIQUE (cart_id, product_id)
);