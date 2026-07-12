package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.CheckoutRequest;
import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.entity.Cart;
import com.antigravity.commerce.entity.Order;
import com.antigravity.commerce.entity.OrderItem;
import com.antigravity.commerce.entity.Product;
import com.antigravity.commerce.entity.User;
import com.antigravity.commerce.exception.ResourceNotFoundException;
import com.antigravity.commerce.mapper.OrderMapper;
import com.antigravity.commerce.repository.CartRepository;
import com.antigravity.commerce.repository.OrderRepository;
import com.antigravity.commerce.repository.ProductRepository;
import com.antigravity.commerce.repository.UserRepository;
import com.antigravity.commerce.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public OrderDto processCheckout(CheckoutRequest request) {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }

        // 1. Calculate Totals
        BigDecimal subTotal = BigDecimal.ZERO;
        for (var item : cart.getItems()) {
            BigDecimal productPrice = item.getProduct().getVariants().stream()
                    .findFirst().map(v -> v.getPrice()).orElse(BigDecimal.ZERO);
            BigDecimal lineTotal = productPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            subTotal = subTotal.add(lineTotal);
        }

        BigDecimal taxTotal = subTotal.multiply(BigDecimal.valueOf(0.1)); // Flat 10% tax for mockup
        BigDecimal shippingTotal = subTotal.compareTo(BigDecimal.valueOf(100)) > 0 ? BigDecimal.ZERO : BigDecimal.valueOf(10.00); // Free shipping over 100
        BigDecimal grandTotal = subTotal.add(taxTotal).add(shippingTotal);

        // 2. Create Order
        Order order = Order.builder()
                .user(user)
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status("PAID") // Assuming mock payment succeeds instantly
                .subTotal(subTotal)
                .taxTotal(taxTotal)
                .shippingTotal(shippingTotal)
                .grandTotal(grandTotal)
                .shippingAddress(request.getShippingAddress())
                .paymentStatus("PAID")
                .build();

        // 3. Create Order Items and Reduce Stock
        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            BigDecimal productPrice = product.getVariants().stream()
                    .findFirst().map(v -> v.getPrice()).orElse(BigDecimal.ZERO);
            
            // Note: In real app with Variants, we would decrement the ProductVariant stock.
            // For now, if stock is added to base product, we could decrement it here.

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .priceAtPurchase(productPrice)
                    .quantity(cartItem.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        // 4. Empty the Cart
        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Successfully processed checkout for user {} - Order {}", user.getEmail(), savedOrder.getOrderNumber());

        return orderMapper.toDto(savedOrder);
    }
}
