package com.antigravity.commerce.service;

import com.antigravity.commerce.dto.BrandDto;
import com.antigravity.commerce.dto.BrandRequest;

import java.util.List;
import java.util.UUID;

public interface BrandService {
    BrandDto createBrand(BrandRequest request);
    BrandDto updateBrand(UUID id, BrandRequest request);
    BrandDto getBrandById(UUID id);
    BrandDto getBrandBySlug(String slug);
    List<BrandDto> getAllBrands();
    void deleteBrand(UUID id);
}
