-- Thêm audit columns vào users
ALTER TABLE users 
ADD COLUMN created_by VARCHAR(255),
ADD COLUMN updated_by VARCHAR(255);

-- Thêm audit columns vào roles (nếu cần audit role)
ALTER TABLE roles
ADD COLUMN created_by VARCHAR(255),
ADD COLUMN updated_by VARCHAR(255);