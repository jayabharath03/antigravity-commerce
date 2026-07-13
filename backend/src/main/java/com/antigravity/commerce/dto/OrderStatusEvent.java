package com.antigravity.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Broadcast on /topic/orders/{orderNumber} whenever an order's status changes. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusEvent {
    private String orderNumber;
    private String status;
}
