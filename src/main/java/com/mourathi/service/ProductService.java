package com.mourathi.service;

import com.mourathi.dto.ProductRequest;
import com.mourathi.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(String category);
    List<ProductResponse> searchProducts(String keyword);
    List<ProductResponse> getInStockProducts();
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
