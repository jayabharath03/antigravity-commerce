package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.ProductDto;
import com.antigravity.commerce.dto.ProductRequest;
import com.antigravity.commerce.entity.Brand;
import com.antigravity.commerce.entity.Category;
import com.antigravity.commerce.entity.Product;
import com.antigravity.commerce.exception.BadRequestException;
import com.antigravity.commerce.exception.ResourceNotFoundException;
import com.antigravity.commerce.mapper.ProductMapper;
import com.antigravity.commerce.repository.BrandRepository;
import com.antigravity.commerce.repository.CategoryRepository;
import com.antigravity.commerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Category category;
    private Brand brand;
    private ProductRequest productRequest;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        UUID categoryId = UUID.randomUUID();
        category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");

        UUID brandId = UUID.randomUUID();
        brand = new Brand();
        brand.setId(brandId);
        brand.setName("Apple");

        UUID productId = UUID.randomUUID();
        product = new Product();
        product.setId(productId);
        product.setName("iPhone 16");
        product.setSlug("iphone-16");
        product.setCategory(category);
        product.setBrand(brand);

        productRequest = ProductRequest.builder()
                .name("iPhone 16")
                .categoryId(categoryId)
                .brandId(brandId)
                .build();

        productDto = ProductDto.builder()
                .id(productId)
                .name("iPhone 16")
                .slug("iphone-16")
                .build();
    }

    @Test
    void createProduct_Success() {
        when(productRepository.existsBySlug("iphone-16")).thenReturn(false);
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(categoryRepository.findById(productRequest.getCategoryId())).thenReturn(Optional.of(category));
        when(brandRepository.findById(productRequest.getBrandId())).thenReturn(Optional.of(brand));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = productService.createProduct(productRequest);

        assertNotNull(result);
        assertEquals("iPhone 16", result.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_ThrowsBadRequest_WhenSlugExists() {
        when(productRepository.existsBySlug("iphone-16")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> productService.createProduct(productRequest));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_Success() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = productService.getProductById(product.getId());

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
    }

    @Test
    void searchProducts_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(productMapper.toDto(product)).thenReturn(productDto);

        Page<ProductDto> result = productService.searchProducts("iphone", "electronics", "apple", null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("iPhone 16", result.getContent().get(0).getName());
    }

    @Test
    void deleteProduct_Success() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        SecurityContextHolder.setContext(securityContext);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.deleteProduct(product.getId());

        verify(productRepository).save(product);
        assertNotNull(product.getDeletedAt());
        assertEquals("admin", product.getDeletedBy());

        SecurityContextHolder.clearContext();
    }
}
