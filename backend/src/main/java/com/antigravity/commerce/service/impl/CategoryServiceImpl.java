package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.CategoryDto;
import com.antigravity.commerce.dto.CategoryRequest;
import com.antigravity.commerce.entity.Category;
import com.antigravity.commerce.exception.BadRequestException;
import com.antigravity.commerce.exception.ResourceNotFoundException;
import com.antigravity.commerce.mapper.CategoryMapper;
import com.antigravity.commerce.repository.CategoryRepository;
import com.antigravity.commerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryRequest request) {
        String slug = generateSlug(request.getName());
        if (categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("Category with this name already exists.");
        }

        Category category = categoryMapper.toEntity(request);
        category.setSlug(slug);

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParent(parent);
        }

        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        categoryMapper.updateEntity(request, category);

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BadRequestException("Category cannot be its own parent.");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Soft delete logic
        category.setDeletedAt(LocalDateTime.now());
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        category.setDeletedBy(currentUser != null ? currentUser : "system");

        categoryRepository.save(category);
    }

    private String generateSlug(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("-$", "");
    }
}
