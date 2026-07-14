package com.antigravity.commerce.controller;

import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.entity.User;
import com.antigravity.commerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getUserOrders(
            @AuthenticationPrincipal User user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        return ResponseEntity.ok(orderService.getUserOrders(user, pageable));
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderDto> getOrderByOrderNumber(
            @PathVariable String orderNumber,
            @AuthenticationPrincipal User user) {
        
        return ResponseEntity.ok(orderService.getOrderByOrderNumber(orderNumber, user));
    }

    @PostMapping("/{orderNumber}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(
            @PathVariable String orderNumber,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(orderService.cancelOrder(orderNumber, user));
    }
}
