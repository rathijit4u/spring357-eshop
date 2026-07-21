package com.mourathi.service;

import com.mourathi.dto.*;
import com.mourathi.entity.CartStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface CartService {
    CartResponse createCart(CartRequest request);
    CartResponse addToCart(UUID id, CartItemRequest request);
    CartResponse removeFromCart(UUID id, UUID cartItemId);
    CartResponse getCartById(UUID id);
    Page<CartResponse> getCarts(Pageable pageable);
    CartResponse getCartByUser(Long userId);
    List<CartResponse> getCartsByStatus(CartStatus status);
    CartResponse updateOrderStatus(UUID id, CartUpdateRequest request);
    boolean clearCart(UUID id);
}
