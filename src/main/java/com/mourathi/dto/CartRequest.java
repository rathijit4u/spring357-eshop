package com.mourathi.dto;

import com.mourathi.entity.CartItem;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CartRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    private final List<CartItemRequest> items = new ArrayList<>();

    public CartRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public List<CartItemRequest> getItems() {
        return items;
    }
}
