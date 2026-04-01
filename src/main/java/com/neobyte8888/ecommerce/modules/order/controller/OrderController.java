package com.neobyte8888.ecommerce.modules.order.controller;

import com.neobyte8888.ecommerce.common.ApiResponse;
import com.neobyte8888.ecommerce.common.PageResponse;
import com.neobyte8888.ecommerce.modules.order.dto.CheckoutRequest;
import com.neobyte8888.ecommerce.modules.order.dto.OrderResponse;
import com.neobyte8888.ecommerce.modules.order.dto.OrderStatusUpdateRequest;
import com.neobyte8888.ecommerce.modules.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // API này chạm đến tiền bạc, CHỈ cho phép ROLE_USER thao tác.
    // (Admin không được phép đặt hàng bằng tài khoản Admin để tránh rối loạn luồng tiền).
    @PostMapping("/checkout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(@Valid @RequestBody CheckoutRequest request) {
        
        // Lấy định danh từ JWT
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Gọi Service chuẩn bị dữ liệu
        OrderResponse data = orderService.createOrder(request, userEmail);
        
        return new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.CREATED.value(), 
                "Khởi tạo đơn hàng thành công", 
                data
        ), HttpStatus.CREATED);
    }
    
    // ==========================================
    // LẤY LỊCH SỬ ĐƠN HÀNG CỦA TÔI
    // ==========================================
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir
    ) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        PageResponse<OrderResponse> data = orderService.getMyOrders(userEmail, page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Lấy lịch sử đơn hàng thành công", 
                data
        ));
    }

    // ==========================================
    // LẤY CHI TIẾT ĐƠN HÀNG (CHỐNG IDOR)
    // ==========================================
    @GetMapping("/me/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrderDetail(@PathVariable Long orderId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        OrderResponse data = orderService.getMyOrderDetail(orderId, userEmail);
        
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Lấy chi tiết đơn hàng thành công", 
                data
        ));
    }
    
    // ==========================================
    // SPRINT 28: ADMIN CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG
    // ==========================================
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        
        OrderResponse data = orderService.updateOrderStatus(orderId, request.getStatus());
        
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Cập nhật trạng thái đơn hàng thành công", 
                data
        ));
    }
}