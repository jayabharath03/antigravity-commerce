package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.BrandDto;
import com.antigravity.commerce.dto.BrandRequest;
import com.antigravity.commerce.entity.Brand;
import com.antigravity.commerce.exception.BadRequestException;
import com.antigravity.commerce.exception.ResourceNotFoundException;
import com.antigravity.commerce.mapper.BrandMapper;
import com.antigravity.commerce.repository.BrandRepository;
import com.antigravity.commerce.service.BrandService;
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
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    @Transactional
    public BrandDto createBrand(BrandRequest request) {
        String slug = generateSlug(request.getName());
        if (brandRepository.existsBySlug(slug)) {
            throw new BadRequestException("Brand with this name already exists.");
        }

        Brand brand = brandMapper.toEntity(request);
        brand.setSlug(slug);

        brand = brandRepository.save(brand);
        return brandMapper.toDto(brand);
    }

    @Override
    @Transactional
    public BrandDto updateBrand(UUID id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        brandMapper.updateEntity(request, brand);

        brand = brandRepository.save(brand);
        return brandMapper.toDto(brand);
    }

    @Override
    public BrandDto getBrandById(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        return brandMapper.toDto(brand);
    }

    @Override
    public BrandDto getBrandBySlug(String slug) {
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        return brandMapper.toDto(brand);
    }

    @Override
    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(brandMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBrand(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        // Soft delete logic
        brand.setDeletedAt(LocalDateTime.now());
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        brand.setDeletedBy(currentUser != null ? currentUser : "system");
        brand.setActive(false);

        brandRepository.save(brand);
    }

    private String generateSlug(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("-$", "");
    }
}
