package com.antigravity.commerce.dto;

import com.antigravity.commerce.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private UUID id;
    private String name;
    private String slug;
    private String shortDescription;
    private String longDescription;
    private CategoryDto category;
    private BrandDto brand;
    private ProductStatus status;
    private String seoTitle;
    private String seoDescription;
    private String taxClass;
    private String hsnCode;
    private String countryOfOrigin;
    private String manufacturer;
    private String warrantyPeriod;
    private Boolean returnable;
    private Integer returnDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ProductVariantDto> variants;
    private List<ProductImageDto> images;
}
