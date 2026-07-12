package com.antigravity.commerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // e.g. "CREDIT_CARD", "PAYPAL"
}
