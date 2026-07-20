package com.mourathi.dto;

import com.mourathi.entity.Order;
import jakarta.validation.constraints.NotNull;

public class OrderUpdateRequest {
    @NotNull(message = "Status is required")
    private Order.Status status;

    public OrderUpdateRequest() {
    }

    public Order.Status getStatus() {
        return status;
    }
}
