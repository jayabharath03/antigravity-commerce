package com.antigravity.commerce.dto;

import com.antigravity.commerce.entity.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String shortDescription;
    private String longDescription;
    
    @NotNull(message = "Category is required")
    private UUID categoryId;
    
    private UUID brandId;
    
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

    // We will typically add variants through a separate endpoint or in the same payload.
    // For simplicity, let's allow basic creation of base product first.
}
