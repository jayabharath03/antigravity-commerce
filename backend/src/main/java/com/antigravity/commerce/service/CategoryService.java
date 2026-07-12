package com.antigravity.commerce.service;

import com.antigravity.commerce.dto.CategoryDto;
import com.antigravity.commerce.dto.CategoryRequest;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryDto createCategory(CategoryRequest request);
    CategoryDto updateCategory(UUID id, CategoryRequest request);
    CategoryDto getCategoryById(UUID id);
    CategoryDto getCategoryBySlug(String slug);
    List<CategoryDto> getAllCategories();
    void deleteCategory(UUID id);
}
