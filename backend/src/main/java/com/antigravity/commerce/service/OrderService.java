package com.antigravity.commerce.service;

import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderDto> getUserOrders(User user, Pageable pageable);
    OrderDto getOrderByOrderNumber(String orderNumber, User user);
    Page<OrderDto> getAllOrders(Pageable pageable);
    OrderDto updateOrderStatus(String orderNumber, String status);
    OrderDto cancelOrder(String orderNumber, User user);
}
