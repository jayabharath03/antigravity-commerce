package com.antigravity.commerce.controller;

import com.antigravity.commerce.dto.ApiResponse;
import com.antigravity.commerce.dto.CartDto;
import com.antigravity.commerce.dto.CartRequest;
import com.antigravity.commerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // Only logged in users can have carts
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getMyCart() {
        return ResponseEntity.ok(ApiResponse.success(cartService.getMyCart(), "Cart fetched successfully"));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addToCart(@Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(ApiResponse.success(cartService.addToCart(request), "Item added to cart"));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDto>> updateQuantity(
            @PathVariable String itemId,
            @RequestBody CartRequest request) {
        return ResponseEntity.ok(ApiResponse.success(cartService.updateQuantity(itemId, request.getQuantity()), "Cart updated"));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(@PathVariable String itemId) {
        return ResponseEntity.ok(ApiResponse.success(cartService.removeItem(itemId), "Item removed from cart"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<CartDto>> clearCart() {
        return ResponseEntity.ok(ApiResponse.success(cartService.clearCart(), "Cart cleared"));
    }
}
