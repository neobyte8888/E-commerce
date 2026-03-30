-- 1. Tạo bảng categories (Danh mục sản phẩm)
-- Dùng BIGSERIAL cho ID để thoải mái lưu trữ.
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE, -- slug dùng để làm URL thân thiện (vd: /danh-muc/dien-thoai-iphone)
    parent_id BIGINT REFERENCES categories(id) ON DELETE SET NULL, -- Tự trỏ lại bảng categories để làm menu đa cấp
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
	updated_by VARCHAR(255)
);

-- Đánh Index cho slug để tăng tốc độ tìm kiếm khi user truy cập URL
CREATE INDEX idx_categories_slug ON categories(slug);

-- 2. Tạo bảng products (Sản phẩm)
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    
    -- TƯ DUY SENIOR: Dùng NUMERIC(19, 2) cho giá tiền.
    -- 19 là tổng số chữ số, 2 là số chữ số phần thập phân. Tuyệt đối an toàn, không có sai số.
    price NUMERIC(19, 2) NOT NULL,
    
    stock INT NOT NULL DEFAULT 0, -- Tồn kho không được phép để null, mặc định là 0
    
    -- Liên kết với danh mục. ON DELETE RESTRICT: Không cho phép xóa Danh mục nếu Danh mục đó vẫn đang chứa Sản phẩm.
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
	updated_by VARCHAR(255)
);

CREATE INDEX idx_products_slug ON products(slug);