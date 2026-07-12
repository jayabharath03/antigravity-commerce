package com.antigravity.commerce.dto;

import com.antigravity.commerce.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDto {
    private UUID id;
    private String sku;
    private String barcode;
    private BigDecimal price;
    private BigDecimal costPrice;
    private Integer stockQuantity;
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private ProductStatus status;
    private Integer lowStockThreshold;
    private Boolean allowBackorder;
    private Integer minOrderQuantity;
    private Integer maxOrderQuantity;
    private List<ProductImageDto> variantImages;
    private List<AttributeValueDto> attributeValues;
}
