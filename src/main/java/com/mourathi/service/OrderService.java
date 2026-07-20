package com.mourathi.service;

import com.mourathi.dto.OrderRequest;
import com.mourathi.dto.OrderResponse;
import com.mourathi.dto.OrderUpdateRequest;
import com.mourathi.entity.Order;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrdersByUser(Long userId);
    List<OrderResponse> getOrdersByStatus(Order.Status status);
    OrderResponse updateOrderStatus(Long id, OrderUpdateRequest request);
    void cancelOrder(Long id);
    void deleteOrder(Long id);
}
