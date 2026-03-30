-- Chạy trên PostgreSQL
-- Enable extension UUID để có thể tự động sinh ra các chuỗi UUID ngẫu nhiên.
-- Tư duy Senior: Dùng UUID cho User, Order... để tránh hacker đoán được số lượng người dùng 
-- (Ví dụ: ID=5 nghĩa là app mới có 5 user, nhưng UUID='550e8400-e29b-41d4-a716-446655440000' thì không ai đoán được).
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- (Optional) Nếu muốn, có thể tạo thêm các Schema hoặc Role tại đây trong tương lai.