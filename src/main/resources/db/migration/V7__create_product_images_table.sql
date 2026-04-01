-- Tạo bảng lưu trữ danh sách ảnh chi tiết của sản phẩm (Gallery)
CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    
    -- Liên kết với bảng products. ON DELETE CASCADE: Xóa sản phẩm thì xóa luôn toàn bộ ảnh chi tiết
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    
    image_url VARCHAR(500) NOT NULL,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
	updated_by VARCHAR(255)
);

-- Đánh index để truy vấn ảnh của 1 sản phẩm nhanh hơn
CREATE INDEX idx_product_images_product_id ON product_images(product_id);