package com.antigravity.commerce.service;

import com.antigravity.commerce.dto.CheckoutRequest;
import com.antigravity.commerce.dto.OrderDto;

public interface CheckoutService {
    OrderDto processCheckout(CheckoutRequest request);
}
