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

    private Cart getNonEmptyCart(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot checkout an empty cart");
        }
        return cart;
    }

    /** Single source of truth for order money: subtotal + 10% tax + shipping (free over 100). */
    private BigDecimal computeGrandTotal(Cart cart) {
        BigDecimal subTotal = computeSubTotal(cart);
        BigDecimal taxTotal = subTotal.multiply(BigDecimal.valueOf(0.1));
        BigDecimal shippingTotal = subTotal.compareTo(BigDecimal.valueOf(100)) > 0
                ? BigDecimal.ZERO : BigDecimal.valueOf(10.00);
        return subTotal.add(taxTotal).add(shippingTotal);
    }

    private BigDecimal computeSubTotal(Cart cart) {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            ProductVariant variant = resolveVariant(item);
            subTotal = subTotal.add(variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return subTotal;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateGrandTotal() {
        User user = getCurrentUser();
        return computeGrandTotal(getNonEmptyCart(user));
    }

    @Override
    @Transactional
    public OrderDto processCheckout(CheckoutRequest request) {
        // Legacy path: place the order without a real payment reference.
        return placeOrder(request.getShippingAddress(), null, null);
    }

    @Override
    @Transactional
    public OrderDto placeOrder(String shippingAddress, String razorpayOrderId, String razorpayPaymentId) {
        User user = getCurrentUser();
        Cart cart = getNonEmptyCart(user);

        BigDecimal subTotal = computeSubTotal(cart);
        BigDecimal taxTotal = subTotal.multiply(BigDecimal.valueOf(0.1));
        BigDecimal shippingTotal = subTotal.compareTo(BigDecimal.valueOf(100)) > 0
                ? BigDecimal.ZERO : BigDecimal.valueOf(10.00);
        BigDecimal grandTotal = subTotal.add(taxTotal).add(shippingTotal);

        Order order = Order.builder()
                .user(user)
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status("PAID")
                .subTotal(subTotal)
                .taxTotal(taxTotal)
                .shippingTotal(shippingTotal)
                .grandTotal(grandTotal)
                .shippingAddress(shippingAddress)
                .paymentStatus("PAID")
                .razorpayOrderId(razorpayOrderId)
                .razorpayPaymentId(razorpayPaymentId)
                .build();

        // Verify stock, then create order items and decrement stock in the same transaction.
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = resolveVariant(cartItem);
            Product product = cartItem.getProduct();
            int requested = cartItem.getQuantity();

            if (!Boolean.TRUE.equals(variant.getAllowBackorder()) && variant.getStockQuantity() < requested) {
                throw new BadRequestException("Insufficient stock for " + product.getName()
                        + " (available: " + variant.getStockQuantity() + ", requested: " + requested + ")");
            }

            variant.setStockQuantity(variant.getStockQuantity() - requested);
            productVariantRepository.save(variant);

            orderItems.add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .variant(variant)
                    .productName(product.getName())
                    .priceAtPurchase(variant.getPrice())
                    .quantity(requested)
                    .build());
        }

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Placed order {} for user {} (razorpayPaymentId={})",
                savedOrder.getOrderNumber(), user.getEmail(), razorpayPaymentId);

        return orderMapper.toDto(savedOrder);
    }
}
