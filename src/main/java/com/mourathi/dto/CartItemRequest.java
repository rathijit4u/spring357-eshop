package com.mourathi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CartItemRequest {
    @NotNull(message = "Product ID is required")
    private UUID productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    public CartItemRequest() {
    }

    public UUID getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
