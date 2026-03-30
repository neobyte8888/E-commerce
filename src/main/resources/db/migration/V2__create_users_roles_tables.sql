-- 1. Tạo bảng roles (Phân quyền)
-- Dùng SERIAL (Tự tăng) cho ID của role vì số lượng role rất ít (USER, ADMIN) và không mang tính nhạy cảm.
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 2. Tạo bảng users (Người dùng)
-- Sử dụng uuid_generate_v4() nhờ vào extension đã bật ở file V1__init_extensions.sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Sẽ lưu chuỗi Hash (BCrypt), KHÔNG lưu plain-text
    full_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- Dùng để khóa tài khoản khi cần (Ban user)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 3. Tạo bảng trung gian user_roles (Quan hệ N-N)
-- Một user có thể có nhiều role, một role có thể cấp cho nhiều user.
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);


-- (Optional) Insert sẵn 2 quyền cơ bản vào hệ thống
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');