package com.neobyte8888.ecommerce.common;

import java.time.LocalDateTime;

/**
 * Lớp chuẩn hóa cấu trúc dữ liệu trả về cho toàn bộ API trong hệ thống.
 * @param <T> Kiểu dữ liệu thực tế sẽ trả về (Ví dụ: UserDTO, ProductResponse, List<Order>...)
 */
public class ApiResponse<T> {
	private int code;
	private String message;
	private T data;
	private LocalDateTime timestamp;
	
	/**
     * Constructor mặc định (Bắt buộc phải có để Jackson thư viện parse JSON hoạt động)
     */
	public ApiResponse() {
		this.timestamp = LocalDateTime.now();
	}
	
	/**
     * Constructor đầy đủ dùng cho các trường hợp trả về cả data (Thành công)
     */
	public ApiResponse(int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
		this.timestamp = LocalDateTime.now();
	}
	
	/**
     * Constructor rút gọn dùng cho các trường hợp không có data trả về (Ví dụ: Báo lỗi, Xóa thành công)
     */
	public ApiResponse(int code, String message) {
		this.code = code;
		this.message = message;
		this.data = null;
		this.timestamp = LocalDateTime.now();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
