package com.mourathi.service;

import com.mourathi.dto.OrderDto;
import com.mourathi.entity.Order;

import java.util.List;

public interface OrderService {
    OrderDto.Response createOrder(OrderDto.Request request);
    OrderDto.Response getOrderById(Long id);
    List<OrderDto.Response> getAllOrders();
    List<OrderDto.Response> getOrdersByUser(Long userId);
    List<OrderDto.Response> getOrdersByStatus(Order.Status status);
    OrderDto.Response updateOrderStatus(Long id, OrderDto.StatusUpdateRequest request);
    void cancelOrder(Long id);
    void deleteOrder(Long id);
}
