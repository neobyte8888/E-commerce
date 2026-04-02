-- Thêm cột version để theo dõi phiên bản của dòng dữ liệu.
-- Set default là 0 cho tất cả các sản phẩm cũ đang có trong Database.
ALTER TABLE products 
ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;