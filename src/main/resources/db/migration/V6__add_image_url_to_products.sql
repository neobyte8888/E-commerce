-- Thêm cột image_url cho bảng products
-- Dùng VARCHAR(500) để đề phòng URL ảnh (sau này lưu trên AWS S3/Cloudinary) có độ dài lớn
ALTER TABLE products ADD COLUMN image_url VARCHAR(500);