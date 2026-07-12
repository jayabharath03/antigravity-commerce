package com.antigravity.commerce.service;

import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.dto.PaymentOrderResponse;
import com.antigravity.commerce.dto.PaymentVerificationRequest;

public interface PaymentService {

    /** Create a Razorpay order for the current cart total so the browser can open checkout. */
    PaymentOrderResponse createOrder();

    /** Verify the Razorpay signature and, only if valid, place the order. */
    OrderDto verifyAndPlaceOrder(PaymentVerificationRequest request);
}
