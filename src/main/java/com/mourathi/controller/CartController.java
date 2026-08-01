package com.mourathi.controller;

import com.mourathi.dto.*;
import com.mourathi.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@Slf4j
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartResponse> createCart(@RequestBody CartRequest cartRequest, UriComponentsBuilder uriComponentsBuilder) {
        CartResponse cartResponse = cartService.createCart(cartRequest);
        URI location = uriComponentsBuilder
                .path("/carts/{id}")
                .buildAndExpand(cartResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(cartResponse);

    }

    @GetMapping
    public ResponseEntity<PageResponse<CartResponse>> getAllCarts(Pageable pageable) {

        Page<CartResponse> cartResponses = cartService.getCarts(pageable);
        PageResponse<CartResponse> response = new PageResponse<>(true, cartResponses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@PathVariable UUID cartId) {
        CartResponse cartResponse = cartService.getCartById(cartId);
        return ResponseEntity.ok(ApiResponse.success(cartResponse));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<CartResponse>> getCartByUser(@PathVariable Long userId) {
        CartResponse cartResponse = cartService.getCartByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(cartResponse));
    }

    @PutMapping("/users/{userId}/additem")
    public ResponseEntity<ApiResponse<CartResponse>> addToCartByUser(
            @PathVariable Long userId, @RequestBody CartRequest cartRequest) {
        CartResponse cartResponse = cartService.getCartByUser(userId);
        UUID cartId = cartResponse.getId();
        for(CartItemRequest cartItemRequest : cartRequest.getItems()) {
            cartResponse = cartService.addToCart(cartResponse.getId(), cartItemRequest);
        }

        return ResponseEntity.ok(ApiResponse.success(cartResponse));
    }

    @PostMapping("{cartId}/checkout")
    public ResponseEntity<ApiResponse<String>> checkout(UUID id) {
        cartService.checkOut(id);
        return ResponseEntity.ok(ApiResponse.success("Success"));
    }

    @PutMapping("/users/{userId}/removeitem")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCartByUser(
            @PathVariable Long userId, @RequestBody List<UUID> cartItemIds) {
        CartResponse cartResponse = cartService.getCartByUser(userId);
        UUID cartId = cartResponse.getId();
        for(UUID cartItemId : cartItemIds) {
            cartResponse = cartService.removeFromCart(cartId, cartItemId);
        }
        return ResponseEntity.ok(ApiResponse.success(cartResponse));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<CartResponse>> clearCartByUser(@PathVariable Long userId) {
        CartResponse cartResponse = cartService.getCartByUser(userId);
        cartService.clearCart(cartResponse.getId());
        return ResponseEntity.ok(ApiResponse.success(cartResponse));
    }
}
