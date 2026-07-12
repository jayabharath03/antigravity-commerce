package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.repository.specification.ProductSpecification;

import com.antigravity.commerce.dto.ProductDto;
import com.antigravity.commerce.dto.ProductRequest;
import com.antigravity.commerce.entity.Brand;
import com.antigravity.commerce.entity.Category;
import com.antigravity.commerce.entity.Product;
import com.antigravity.commerce.entity.ProductStatus;
import com.antigravity.commerce.exception.BadRequestException;
import com.antigravity.commerce.exception.ResourceNotFoundException;
import com.antigravity.commerce.mapper.ProductMapper;
import com.antigravity.commerce.repository.BrandRepository;
import com.antigravity.commerce.repository.CategoryRepository;
import com.antigravity.commerce.repository.ProductRepository;
import com.antigravity.commerce.service.ProductService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDto createProduct(ProductRequest request) {
        String slug = generateSlug(request.getName());
        if (productRepository.existsBySlug(slug)) {
            throw new BadRequestException("Product with this name already exists.");
        }

        Product product = productMapper.toEntity(request);
        product.setSlug(slug);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        product.setCategory(category);

        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
            product.setBrand(brand);
        }

        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        productMapper.updateEntity(request, product);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        product.setCategory(category);

        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
            product.setBrand(brand);
        } else {
            product.setBrand(null);
        }

        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    public ProductDto getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toDto(product);
    }

    @Override
    public ProductDto getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toDto(product);
    }

    @Override
    public Page<ProductDto> searchProducts(String search, String categorySlug, String brandSlug, Double minPrice, Double maxPrice, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.filterProducts(search, categorySlug, brandSlug, minPrice, maxPrice);
        return productRepository.findAll(spec, pageable).map(productMapper::toDto);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Soft delete logic
        product.setDeletedAt(LocalDateTime.now());
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        product.setDeletedBy(currentUser != null ? currentUser : "system");
        product.setStatus(ProductStatus.ARCHIVED);

        productRepository.save(product);
    }

    private String generateSlug(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("-$", "");
    }
}
