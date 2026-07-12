package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.entity.Order;
import com.antigravity.commerce.entity.User;
import com.antigravity.commerce.mapper.OrderMapper;
import com.antigravity.commerce.repository.OrderRepository;
import com.antigravity.commerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.antigravity.commerce.exception.ResourceNotFoundException;
import com.antigravity.commerce.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getUserOrders(User user, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUser(user, pageable);
        return orders.map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderByOrderNumber(String orderNumber, User user) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You do not have permission to view this order.");
        }
        
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(String orderNumber, String status) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        order.setStatus(status);
        order = orderRepository.save(order);
        
        return orderMapper.toDto(order);
    }
}
