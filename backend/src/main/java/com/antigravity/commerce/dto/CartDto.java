package com.antigravity.commerce.dto;

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
public class CartDto {
    private UUID id;
    private String sessionId;
    private List<CartItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
