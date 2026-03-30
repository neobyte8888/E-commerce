-- Thêm cờ is_deleted cho bảng categories
ALTER TABLE categories ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Thêm cờ is_deleted cho bảng products
ALTER TABLE products ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- (Tùy chọn) Đánh Index cho cột is_deleted vì ta sẽ luôn query WHERE is_deleted = false
CREATE INDEX idx_categories_is_deleted ON categories(is_deleted);

CREATE INDEX idx_products_is_deleted ON products(is_deleted);