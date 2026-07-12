package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.CategoryDto;
import com.antigravity.commerce.dto.CategoryRequest;
import com.antigravity.commerce.entity.Category;
import com.antigravity.commerce.exception.BadRequestException;
import com.antigravity.commerce.exception.ResourceNotFoundException;
import com.antigravity.commerce.mapper.CategoryMapper;
import com.antigravity.commerce.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequest categoryRequest;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        category = new Category();
        category.setId(id);
        category.setName("Electronics");
        category.setSlug("electronics");

        categoryRequest = CategoryRequest.builder()
                .name("Electronics")
                .description("Electronic items")
                .build();

        categoryDto = CategoryDto.builder()
                .id(id)
                .name("Electronics")
                .slug("electronics")
                .description("Electronic items")
                .build();
    }

    @Test
    void createCategory_Success() {
        when(categoryRepository.existsBySlug("electronics")).thenReturn(false);
        when(categoryMapper.toEntity(categoryRequest)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.createCategory(categoryRequest);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        assertEquals("electronics", result.getSlug());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_ThrowsBadRequest_WhenSlugExists() {
        when(categoryRepository.existsBySlug("electronics")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> categoryService.createCategory(categoryRequest));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getCategoryById_Success() {
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getCategoryById(category.getId());

        assertNotNull(result);
        assertEquals(category.getId(), result.getId());
    }

    @Test
    void getCategoryById_ThrowsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(id));
    }
    
    @Test
    void deleteCategory_Success() {
        // Mock Security Context for the deletedBy field
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        SecurityContextHolder.setContext(securityContext);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        categoryService.deleteCategory(category.getId());

        verify(categoryRepository).save(category);
        assertNotNull(category.getDeletedAt());
        assertEquals("admin", category.getDeletedBy());
        
        SecurityContextHolder.clearContext();
    }
}
