-- 1. Bảng Đơn hàng (Orders)
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    total_amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    shipping_address TEXT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
	updated_by VARCHAR(255)
);
-- Đánh Index để tăng tốc query lịch sử mua hàng của 1 user
CREATE INDEX idx_orders_user_id ON orders(user_id);

-- 2. Bảng Chi tiết Đơn hàng (Order Items)
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    
    quantity INT NOT NULL CHECK (quantity > 0),
    
    -- BẮT BUỘC phải có cột giá tại thời điểm mua để đóng băng lịch sử
    price NUMERIC(19, 2) NOT NULL,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
	updated_by VARCHAR(255)
);