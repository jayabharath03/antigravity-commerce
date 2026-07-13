package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.repository.specification.ProductSpecification;

import com.antigravity.commerce.dto.ProductDto;
import com.antigravity.commerce.dto.ProductRequest;
import com.antigravity.commerce.entity.Brand;
import com.antigravity.commerce.entity.Category;
import com.antigravity.commerce.entity.Product;
import com.antigravity.commerce.entity.ProductImage;
import com.antigravity.commerce.entity.ProductStatus;
import com.antigravity.commerce.entity.ProductVariant;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        if (product.getStatus() == null) {
            product.setStatus(ProductStatus.ACTIVE);
        }

        // Create the default variant (price + stock) so the product is immediately sellable.
        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(resolveSku(request.getSku(), slug))
                .price(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO)
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .status(ProductStatus.ACTIVE)
                .build();
        product.getVariants().add(variant);

        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            product.getImages().add(ProductImage.builder()
                    .product(product)
                    .imageUrl(request.getImageUrl())
                    .isPrimary(true)
                    .displayOrder(0)
                    .build());
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

        // Update (or create) the default variant's price/stock.
        if (request.getPrice() != null || request.getStockQuantity() != null) {
            ProductVariant variant = product.getVariants().stream().findFirst().orElse(null);
            if (variant == null) {
                variant = ProductVariant.builder()
                        .product(product)
                        .sku(resolveSku(request.getSku(), product.getSlug()))
                        .status(ProductStatus.ACTIVE)
                        .build();
                product.getVariants().add(variant);
            }
            if (request.getPrice() != null) variant.setPrice(request.getPrice());
            if (request.getStockQuantity() != null) variant.setStockQuantity(request.getStockQuantity());
        }

        // Update (or add) the primary image.
        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            ProductImage image = product.getImages().stream().findFirst().orElse(null);
            if (image == null) {
                product.getImages().add(ProductImage.builder()
                        .product(product).imageUrl(request.getImageUrl()).isPrimary(true).displayOrder(0).build());
            } else {
                image.setImageUrl(request.getImageUrl());
            }
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

    /** Use the provided SKU, or derive a unique one from the slug. */
    private String resolveSku(String sku, String slug) {
        if (sku != null && !sku.isBlank()) {
            return sku.trim();
        }
        String base = slug.toUpperCase().replaceAll("[^A-Z0-9]", "");
        return base + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
