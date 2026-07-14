package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.entity.Order;
import com.antigravity.commerce.entity.OrderItem;
import com.antigravity.commerce.entity.ProductVariant;
import com.antigravity.commerce.entity.User;
import com.antigravity.commerce.mapper.OrderMapper;
import com.antigravity.commerce.repository.OrderRepository;
import com.antigravity.commerce.service.OrderService;
import com.antigravity.commerce.service.RealtimeEventPublisher;
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
    private final RealtimeEventPublisher realtimeEventPublisher;

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

        // Push the new status to the customer's open order page.
        realtimeEventPublisher.publishOrderStatus(order.getOrderNumber(), order.getStatus());

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(String orderNumber, User user) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You do not have permission to cancel this order.");
        }

        String status = order.getStatus();
        if ("SHIPPED".equals(status) || "DELIVERED".equals(status)) {
            throw new BadRequestException("This order is already " + status.toLowerCase() + " and can no longer be cancelled.");
        }
        if ("CANCELLED".equals(status)) {
            throw new BadRequestException("This order is already cancelled.");
        }

        // Put the reserved stock back and push the restored levels live.
        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getVariant();
            if (variant != null) {
                variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
                realtimeEventPublisher.publishStockUpdate(item.getProduct().getId(), variant.getId(), variant.getStockQuantity());
            }
        }

        order.setStatus("CANCELLED");
        order = orderRepository.save(order);
        realtimeEventPublisher.publishOrderStatus(order.getOrderNumber(), order.getStatus());

        return orderMapper.toDto(order);
    }
}
