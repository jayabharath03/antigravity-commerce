package com.antigravity.commerce.controller;

import com.antigravity.commerce.dto.ApiResponse;
import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.dto.PaymentOrderResponse;
import com.antigravity.commerce.dto.PaymentVerificationRequest;
import com.antigravity.commerce.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class PaymentController {

    private final PaymentService paymentService;

    /** Step 1: create a Razorpay order for the current cart total. */
    @PostMapping("/order")
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> createOrder() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.createOrder(), "Payment order created"));
    }

    /** Step 2: verify the signature returned by the Razorpay popup and place the order. */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<OrderDto>> verify(@Valid @RequestBody PaymentVerificationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.verifyAndPlaceOrder(request), "Payment verified. Order placed."));
    }
}
