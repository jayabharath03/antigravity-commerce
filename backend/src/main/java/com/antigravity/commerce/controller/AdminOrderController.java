package com.antigravity.commerce.controller;

import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @PatchMapping("/{orderNumber}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable String orderNumber,
            @RequestBody Map<String, String> payload) {
        
        String status = payload.get("status");
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        
        return ResponseEntity.ok(orderService.updateOrderStatus(orderNumber, status));
    }
}
