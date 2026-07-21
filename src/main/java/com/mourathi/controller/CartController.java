package com.mourathi.controller;

import com.mourathi.dto.*;
import com.mourathi.service.CartService;
import com.mourathi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@Slf4j
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<CartResponse>> getAllCarts(Pageable pageable) {
        Page<CartResponse> cartResponses = cartService.getCarts(pageable);
        PageResponse<CartResponse> response = new PageResponse<>(true, cartResponses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cartId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@PathVariable UUID cartId) {
        CartResponse cartResponse = cartService.getCartById(cartId);
        return ResponseEntity.ok(ApiResponse.success(cartResponse));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<CartResponse>> getCartByUser(@PathVariable Long userId,
            Authentication authentication) {
        CartResponse cartResponse = cartService.getCartByUser(userId);
        Long currentUserId = userService.getUserByUserName(authentication.getName()).getId();
        log.info(authentication.getPrincipal().toString());
        if (cartResponse.getUserId().equals(currentUserId))
            return ResponseEntity.ok(ApiResponse.success(cartResponse));
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
