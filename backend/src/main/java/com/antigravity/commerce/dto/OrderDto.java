package com.antigravity.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID id;
    private String orderNumber;
    private String status;
    private BigDecimal subTotal;
    private BigDecimal taxTotal;
    private BigDecimal shippingTotal;
    private BigDecimal grandTotal;
    private String shippingAddress;
    private String paymentStatus;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
}
