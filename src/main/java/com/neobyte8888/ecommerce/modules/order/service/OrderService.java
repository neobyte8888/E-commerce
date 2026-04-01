package com.neobyte8888.ecommerce.modules.order.service;

import com.neobyte8888.ecommerce.common.PageResponse;
import com.neobyte8888.ecommerce.modules.order.dto.CheckoutRequest;
import com.neobyte8888.ecommerce.modules.order.dto.OrderResponse;
import com.neobyte8888.ecommerce.modules.order.enums.OrderStatus;

public interface OrderService {
    OrderResponse createOrder(CheckoutRequest request, String userEmail);
    PageResponse<OrderResponse> getMyOrders(String userEmail, int pageNo, int pageSize, String sortBy, String sortDir);
    OrderResponse getMyOrderDetail(Long orderId, String userEmail);
    // Cập nhật trạng thái (Admin)
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);
}