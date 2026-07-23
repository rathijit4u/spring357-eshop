package com.mourathi;

import com.mourathi.dto.ProductResponse;
import com.mourathi.entity.Product;
import com.mourathi.repository.ProductRepository;
import com.mourathi.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void testGetAllProducts() {
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .sku("xyz")
                .price(BigDecimal.valueOf(100.00))
                .name("Pen")
                .description("Lamy Safari")
                .stockQuantity(10)
                .category("Office Stationary")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.findAll()).thenReturn(List.of(product));
        List<ProductResponse> products = productService.getAllProducts();
        assertEquals(1, products.size());
    }
}
