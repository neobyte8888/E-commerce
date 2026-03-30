package com.neobyte8888.ecommerce.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Lớp cơ sở chứa các trường kiểm toán (Audit) dùng chung cho toàn bộ Entity trong hệ thống.
 */
@MappedSuperclass // Đánh dấu đây là class cha, các thuộc tính của nó sẽ được đẩy xuống các bảng con
@EntityListeners(AuditingEntityListener.class) // Kích hoạt Listener để Spring tự động gán giá trị thời gian
public abstract class BaseEntity {

    @CreatedDate // Spring sẽ tự động lấy thời gian hiện tại khi bản ghi được INSERT
    @Column(name = "created_at", nullable = false, updatable = false) // updatable = false: Cấm update cột này sau khi đã tạo
    private LocalDateTime createdAt;

    @LastModifiedDate // Spring sẽ tự động cập nhật thời gian mỗi khi bản ghi bị UPDATE
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    // --------------------------------------------------------
    // GETTERS & SETTERS (Viết tay, không dùng Lombok)
    // --------------------------------------------------------

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
    
}