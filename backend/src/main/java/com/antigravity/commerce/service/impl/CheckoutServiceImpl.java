package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.CheckoutRequest;
import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.entity.Cart;
import com.antigravity.commerce.entity.CartItem;
import com.antigravity.commerce.entity.Order;
import com.antigravity.commerce.entity.OrderItem;
import com.antigravity.commerce.entity.Product;
import com.antigravity.commerce.entity.ProductVariant;
import com.antigravity.commerce.entity.User;
import com.antigravity.commerce.exception.BadRequestException;
import com.antigravity.commerce.exception.ResourceNotFoundException;
import com.antigravity.commerce.mapper.OrderMapper;
import com.antigravity.commerce.repository.CartRepository;
import com.antigravity.commerce.repository.OrderRepository;
import com.antigravity.commerce.repository.ProductRepository;
import com.antigravity.commerce.repository.ProductVariantRepository;
import com.antigravity.commerce.repository.UserRepository;
import com.antigravity.commerce.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /** The variant chosen at add-to-cart time, or the product's default variant for legacy carts. */
    private ProductVariant resolveVariant(CartItem item) {
        if (item.getVariant() != null) {
            return item.getVariant();
        }
        return item.getProduct().getVariants().stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        "Product '" + item.getProduct().getName() + "' has no purchasable variant"));
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

        // 1. Resolve each line to its chosen variant, verify stock, and compute totals.
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            ProductVariant variant = resolveVariant(item);
            int requested = item.getQuantity();
            if (!Boolean.TRUE.equals(variant.getAllowBackorder()) && variant.getStockQuantity() < requested) {
                throw new BadRequestException("Insufficient stock for " + item.getProduct().getName()
                        + " (available: " + variant.getStockQuantity() + ", requested: " + requested + ")");
            }
            subTotal = subTotal.add(variant.getPrice().multiply(BigDecimal.valueOf(requested)));
        }

        BigDecimal taxTotal = subTotal.multiply(BigDecimal.valueOf(0.1)); // Flat 10% tax for mockup
        BigDecimal shippingTotal = subTotal.compareTo(BigDecimal.valueOf(100)) > 0 ? BigDecimal.ZERO : BigDecimal.valueOf(10.00); // Free shipping over 100
        BigDecimal grandTotal = subTotal.add(taxTotal).add(shippingTotal);

        // 2. Create Order
        // NOTE: payment is still mocked as PAID here. Real gateway confirmation is Phase C in PROJECT_STATUS.md.
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

        // 3. Create order items and decrement stock now that we know every line has enough.
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = resolveVariant(cartItem);
            Product product = cartItem.getProduct();

            variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
            productVariantRepository.save(variant);

            orderItems.add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .variant(variant)
                    .productName(product.getName())
                    .priceAtPurchase(variant.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build());
        }

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        // 4. Empty the Cart
        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Successfully processed checkout for user {} - Order {}", user.getEmail(), savedOrder.getOrderNumber());

        return orderMapper.toDto(savedOrder);
    }
}
