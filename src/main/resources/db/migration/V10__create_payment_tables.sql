-- Tạo bảng lưu trữ Lịch sử Thanh toán
CREATE TABLE payments (
    -- Dùng UUID cho ID thanh toán để bảo mật, 
    -- tránh việc Hacker đoán được số lượng giao dịch của công ty trong ngày (nếu dùng BIGSERIAL 1, 2, 3...)
    id UUID PRIMARY KEY,
    
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    amount NUMERIC(19, 2) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    
    -- Cột này cực kỳ quan trọng: Dùng để đối soát (Reconciliation) với bên thứ 3 (VD: Mã GD của VNPay)
    provider_transaction_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Đảm bảo tính Toàn vẹn dữ liệu (Data Integrity).
-- Một đơn hàng chỉ được phép liên kết với 1 phiên thanh toán thành công duy nhất để tránh double-charge (trừ tiền 2 lần).
CREATE UNIQUE INDEX idx_payments_order_id ON payments(order_id);