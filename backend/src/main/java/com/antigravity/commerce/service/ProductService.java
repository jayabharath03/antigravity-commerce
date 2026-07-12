package com.antigravity.commerce.service;

import com.antigravity.commerce.dto.ProductDto;
import com.antigravity.commerce.dto.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {
    ProductDto createProduct(ProductRequest request);
    ProductDto updateProduct(UUID id, ProductRequest request);
    ProductDto getProductById(UUID id);
    ProductDto getProductBySlug(String slug);
    Page<ProductDto> searchProducts(String search, String categorySlug, String brandSlug, Double minPrice, Double maxPrice, Pageable pageable);
    void deleteProduct(UUID id);
}
