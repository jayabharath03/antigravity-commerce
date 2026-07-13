package com.antigravity.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Broadcast on /topic/stock whenever a variant's stock level changes. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateEvent {
    private UUID productId;
    private UUID variantId;
    private Integer stockQuantity;
}
