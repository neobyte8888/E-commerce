-- ==========================================
-- ATABASE INDEXING CHO MỆNH ĐỀ WHERE
-- ==========================================

-- 1. Tối ưu Đăng nhập (Phase 2): Tăng tốc độ tìm kiếm User theo Email
-- (Mặc dù UNIQUE đã có index, nhưng tạo tường minh giúp dễ quản lý cấu trúc)
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- 2. Tối ưu SEO và Hiển thị (Phase 3): Tăng tốc độ tìm kiếm Product theo Slug
-- Slug được gọi hàng vạn lần mỗi khi User lướt trang chủ và bấm vào xem chi tiết Sản phẩm
CREATE INDEX IF NOT EXISTS idx_products_slug ON products(slug);

-- 3. Tối ưu Lịch sử mua hàng (Phase 5): Tăng tốc query Order theo User ID
-- Cứu cánh cho API /api/v1/orders/me khi User có quá nhiều đơn hàng
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);

-- (Bonus bổ sung): Tối ưu tìm kiếm Sản phẩm theo Danh mục
-- Khi khách lướt danh mục "Điện thoại", hệ thống phải query toàn bộ Product có category_id tương ứng. 
-- Cột này xuất hiện trong mệnh đề WHERE liên tục, bắt buộc phải có Index.
CREATE INDEX IF NOT EXISTS idx_products_category_id ON products(category_id);