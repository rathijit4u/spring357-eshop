package com.mourathi.service;

import com.mourathi.dto.ProductRequest;
import com.mourathi.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(UUID id);
    List<ProductResponse> getAllProducts();
    Page<ProductResponse> getAllProducts(Pageable pageable);
    List<ProductResponse> getProductsByCategory(String category);
    List<ProductResponse> searchProducts(String keyword);
    List<ProductResponse> getInStockProducts();
    ProductResponse updateProduct(UUID id, ProductRequest request);
    void deleteProduct(UUID id);
}
