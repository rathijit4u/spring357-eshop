package com.mourathi.service;

import com.mourathi.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto.Response createProduct(ProductDto.Request request);
    ProductDto.Response getProductById(Long id);
    List<ProductDto.Response> getAllProducts();
    List<ProductDto.Response> getProductsByCategory(String category);
    List<ProductDto.Response> searchProducts(String keyword);
    List<ProductDto.Response> getInStockProducts();
    ProductDto.Response updateProduct(Long id, ProductDto.Request request);
    void deleteProduct(Long id);
}
