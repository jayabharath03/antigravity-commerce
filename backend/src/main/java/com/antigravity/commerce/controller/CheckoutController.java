package com.antigravity.commerce.controller;

import com.antigravity.commerce.dto.ApiResponse;
import com.antigravity.commerce.dto.CheckoutRequest;
import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // Only logged in users can checkout
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> processCheckout(@Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(ApiResponse.success(checkoutService.processCheckout(request), "Checkout successful. Order Placed."));
    }
}
