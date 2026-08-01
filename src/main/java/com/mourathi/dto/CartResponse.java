package com.mourathi.dto;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public class CartResponse {
    private UUID id;
    private Long userId;
    private String userName;
    private List<CartItemResponse> cartItems;
    private int totalItems;
    private BigDecimal totalPrice;

    public UUID getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public List<CartItemResponse> getCartItems() {
        return cartItems;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}
