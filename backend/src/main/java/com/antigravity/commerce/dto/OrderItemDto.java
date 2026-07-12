package com.antigravity.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private UUID id;
    private ProductDto product;
    private String productName;
    private BigDecimal priceAtPurchase;
    private Integer quantity;
}
