package com.antigravity.commerce.controller;

import com.antigravity.commerce.dto.BrandDto;
import com.antigravity.commerce.dto.BrandRequest;
import com.antigravity.commerce.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrandById(@PathVariable UUID id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<BrandDto> getBrandBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(brandService.getBrandBySlug(slug));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandDto> createBrand(@Valid @RequestBody BrandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.createBrand(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandDto> updateBrand(@PathVariable UUID id, @Valid @RequestBody BrandRequest request) {
        return ResponseEntity.ok(brandService.updateBrand(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBrand(@PathVariable UUID id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
