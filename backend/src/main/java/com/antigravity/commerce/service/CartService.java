package com.antigravity.commerce.service;

import com.antigravity.commerce.dto.CartDto;
import com.antigravity.commerce.dto.CartRequest;

public interface CartService {
    CartDto getMyCart();
    CartDto addToCart(CartRequest request);
    CartDto updateQuantity(String itemId, Integer quantity);
    CartDto removeItem(String itemId);
    CartDto clearCart();
}
