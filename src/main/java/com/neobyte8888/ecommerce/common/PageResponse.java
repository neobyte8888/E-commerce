package com.neobyte8888.ecommerce.common;

import java.util.List;

/**
 * Lớp bọc dữ liệu phân trang chuẩn hóa trả về cho Frontend.
 */
public class PageResponse<T> {
	private List<T> content; // Danh sách dữ liệu của trang hiện tại
	private int pageNo; // Trang hiện tại (bắt đầu từ 0)
	private int pageSize; // Số phần tử trên 1 trang
	private long totalElements; // Tổng số phần tử trong db
	private int totalPages; // Tổng số trang
	private boolean last; // Có phải trang cuối cùng không

	// Contructor
	public PageResponse() {

	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

}
