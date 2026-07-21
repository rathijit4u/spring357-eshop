package com.mourathi.service.impl;

import com.mourathi.dto.*;
import com.mourathi.entity.*;
import com.mourathi.exception.DuplicateResourceException;
import com.mourathi.exception.ResourceNotFoundException;
import com.mourathi.repository.CartRepository;
import com.mourathi.repository.ProductRepository;
import com.mourathi.repository.UserRepository;
import com.mourathi.service.CartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CartResponse createCart(CartRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User Id '%s' not found".formatted(request.getUserId())));

        if(!cartRepository.findByUserId(user.getId()).isEmpty()){
            throw new DuplicateResourceException("Cart for userId '%s' is already in use".formatted(user.getId()));
        }

        Cart cart = Cart.builder().user(user)
                .build();

        List<CartItem> items = request.getItems().stream()
                .map(cartItemRequest -> {
                    Product product = productRepository.findById(cartItemRequest.getProductId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Product Id '%s' not found".formatted(cartItemRequest.getProductId())));
                    return CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(cartItemRequest.getQuantity())
                            .build();
                }).toList();

        cart.setItems(items);
        cartRepository.save(cart);
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse addToCart(UUID id, CartItemRequest request) {
        return null;
    }

    @Override
    public CartResponse removeFromCart(UUID id, UUID cartItemId) {
        return null;
    }

    @Override
    public CartResponse getCartById(UUID id) {
        Cart cart =  cartRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cart Id '%s' not found".formatted(id))
        );
        return mapToCartResponse(cart);
    }

    @Override
    public Page<CartResponse> getCarts(Pageable pageable) {
        return cartRepository.findAll(pageable)
                .map(this::mapToCartResponse);
    }

    @Override
    public CartResponse getCartByUser(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        if(carts.isEmpty()){
            throw new ResourceNotFoundException("No Cart is found for user id '%s'".formatted(userId));
        }
        return mapToCartResponse(carts.getFirst());
    }


    @Override
    public List<CartResponse> getCartsByStatus(CartStatus status) {
        return cartRepository.findByStatus(status)
                .stream().map(this::mapToCartResponse).collect(Collectors.toList());
    }

    @Override
    public CartResponse updateOrderStatus(UUID id, CartUpdateRequest request) {
        Cart cart =  cartRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cart Id '%s' not found".formatted(id))
        );
        cart.setStatus(request.getStatus());
        return mapToCartResponse(cartRepository.save(cart));
    }

    @Override
    public boolean clearCart(UUID id) {
        cartRepository.deleteById(id);
        return true;
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> responseItems = new ArrayList<>();
        int totalItems = 0;
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        for (CartItem item : cart.getItems()) {
            totalItems += item.getQuantity();
            BigDecimal itemPrice = item.getProduct().getPrice()
                    .multiply(new BigDecimal(item.getQuantity()));
            totalPrice = totalPrice.add(itemPrice);
            responseItems.add(CartItemResponse.builder()
                    .cartItemId(item.getId())
                    .productId(item.getProduct().getId())
                    .quantity(item.getQuantity())
                    .productName(item.getProduct().getName())
                    .unitPrice(item.getProduct().getPrice())
                    .build());
        }

        return CartResponse.builder()
                .id(cart.getId())
                .cartItems(responseItems)
                .userId(cart.getUser().getId())
                .userName(cart.getUser().getFirstName() + " "
                        + cart.getUser().getLastName())
                .totalPrice(totalPrice)
                .totalItems(totalItems)
                .build();
    }
}
