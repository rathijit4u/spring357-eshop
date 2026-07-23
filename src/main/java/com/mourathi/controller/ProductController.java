package com.mourathi.controller;

import com.mourathi.dto.ApiResponse;
import com.mourathi.dto.PageResponse;
import com.mourathi.dto.ProductRequest;
import com.mourathi.dto.ProductResponse;
import com.mourathi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@PreAuthorize("@rolePermissionEvaluator.hasRole(authentication, 'ROLE_CUSTOMER')")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", response));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> createProductBulk(
            @Valid @RequestBody List<ProductRequest> requests) {
        List<ProductResponse> productResponses = new ArrayList<>();
        for (ProductRequest request : requests) {
            productResponses.add(productService.createProduct(request));

        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Products created successfully", productResponses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(Pageable pageable) {
        Page<ProductResponse> productResponses = productService.getAllProducts(pageable);
        PageResponse<ProductResponse> response = new PageResponse<>(true, productResponses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/in-stock")
    public ResponseEntity<PageResponse<ProductResponse>> getInStockProducts(Pageable pageable) {
        Page<ProductResponse> productResponses = productService.getInStockProducts(pageable);
        PageResponse<ProductResponse> response = new PageResponse<>(true, productResponses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductsByCategory(category)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchProducts(keyword)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Product updated successfully", productService.updateProduct(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}
