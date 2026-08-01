package com.mourathi.service.impl;

import com.mourathi.dto.ProductRequest;
import com.mourathi.dto.ProductResponse;
import com.mourathi.entity.Product;
import com.mourathi.exception.DuplicateResourceException;
import com.mourathi.exception.ResourceNotFoundException;
import com.mourathi.repository.ProductRepository;
import com.mourathi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    public ProductResponse createProduct(ProductRequest request) {
        if (request.getSku() != null && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product with SKU '" + request.getSku() + "' already exists");
        }
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .category(request.getCategory())
                .sku(request.getSku())
                .build();
        return mapToResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    public ProductResponse getProductById(UUID id) {
        return mapToResponse(findProductOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    public Page<ProductResponse> getInStockProducts(Pageable pageable) {
        return productRepository.findAllInStock(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = findProductOrThrow(id);
        if (request.getSku() != null && !request.getSku().equals(product.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("SKU '" + request.getSku() + "' is already in use");
        }
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setSku(request.getSku());
        return mapToResponse(productRepository.save(product));
    }

    @Override
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private Product findProductOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .sku(product.getSku())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
