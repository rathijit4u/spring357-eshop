package com.mourathi.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public class CartItemResponse {
    private UUID cartItemId;
    private UUID productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;

    public CartItemResponse(UUID cartItemId, UUID productId, String productName, int quantity, BigDecimal unitPrice) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public UUID getCartItemId() {
        return cartItemId;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
