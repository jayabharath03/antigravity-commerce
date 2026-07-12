package com.antigravity.commerce.service;

import com.antigravity.commerce.dto.CheckoutRequest;
import com.antigravity.commerce.dto.OrderDto;

import java.math.BigDecimal;

public interface CheckoutService {

    /** Legacy single-step checkout (mock payment). Kept for backward compatibility. */
    OrderDto processCheckout(CheckoutRequest request);

    /** Grand total of the current user's cart, used to create a payment order. */
    BigDecimal calculateGrandTotal();

    /**
     * Place the order after payment has been confirmed. Verifies stock, decrements it,
     * records the Razorpay references, and clears the cart.
     */
    OrderDto placeOrder(String shippingAddress, String razorpayOrderId, String razorpayPaymentId);
}
