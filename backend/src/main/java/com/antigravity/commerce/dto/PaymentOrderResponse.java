package com.antigravity.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Returned to the frontend so it can open the Razorpay checkout popup.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {
    private String razorpayOrderId;   // id of the order created at Razorpay
    private String razorpayKeyId;     // public key id the browser needs
    private Long amount;              // amount in the smallest currency unit (paise)
    private String currency;          // e.g. INR
    private BigDecimal grandTotal;    // human-readable total for display
    private boolean mock;             // true when no gateway keys are configured (simulated payment)
}
