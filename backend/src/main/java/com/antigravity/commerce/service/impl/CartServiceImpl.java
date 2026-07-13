package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.CartDto;
import com.antigravity.commerce.dto.CartRequest;
import com.antigravity.commerce.entity.Cart;
import com.antigravity.commerce.entity.CartItem;
import com.antigravity.commerce.entity.Product;
import com.antigravity.commerce.entity.ProductVariant;
import com.antigravity.commerce.entity.User;
import com.antigravity.commerce.exception.BadRequestException;
import com.antigravity.commerce.exception.ResourceNotFoundException;
import com.antigravity.commerce.mapper.CartMapper;
import com.antigravity.commerce.repository.CartRepository;
import com.antigravity.commerce.repository.ProductRepository;
import com.antigravity.commerce.repository.ProductVariantRepository;
import com.antigravity.commerce.repository.UserRepository;
import com.antigravity.commerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = Cart.builder().user(user).build();
                    return cartRepository.save(cart);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getMyCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartDto addToCart(CartRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        ProductVariant variant = resolveVariant(product, request.getVariantId());

        // Same product AND same variant = same cart line; different variants stay separate.
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId())
                        && sameVariant(item.getVariant(), variant))
                .findFirst();

        int currentQty = existingItem.map(CartItem::getQuantity).orElse(0);
        assertStock(variant, currentQty + request.getQuantity(), product.getName());

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .variant(variant)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    /**
     * Resolve which variant the customer is buying. If a variantId is supplied it must belong to
     * the product; otherwise fall back to the product's first (default) variant.
     */
    private ProductVariant resolveVariant(Product product, UUID variantId) {
        if (variantId != null) {
            ProductVariant variant = productVariantRepository.findById(variantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));
            if (!variant.getProduct().getId().equals(product.getId())) {
                throw new BadRequestException("Variant does not belong to the selected product");
            }
            return variant;
        }
        return product.getVariants().stream().findFirst().orElse(null);
    }

    private boolean sameVariant(ProductVariant a, ProductVariant b) {
        if (a == null || b == null) {
            return a == b;
        }
        return a.getId().equals(b.getId());
    }

    /** Reject cart quantities that exceed available stock (unless backorders are allowed). */
    private void assertStock(ProductVariant variant, int desiredQty, String productName) {
        if (variant != null && !Boolean.TRUE.equals(variant.getAllowBackorder())
                && variant.getStockQuantity() < desiredQty) {
            throw new BadRequestException("Only " + variant.getStockQuantity()
                    + " of " + productName + " left in stock");
        }
    }

    private ProductVariant variantOf(CartItem item) {
        if (item.getVariant() != null) return item.getVariant();
        return item.getProduct().getVariants().stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public CartDto updateQuantity(String itemId, Integer quantity) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId().toString().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        if (quantity <= 0) {
            cart.getItems().remove(itemToUpdate);
        } else {
            assertStock(variantOf(itemToUpdate), quantity, itemToUpdate.getProduct().getName());
            itemToUpdate.setQuantity(quantity);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    @Transactional
    public CartDto removeItem(String itemId) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        cart.getItems().removeIf(item -> item.getId().toString().equals(itemId));

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    @Transactional
    public CartDto clearCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        
        cart.getItems().clear();
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }
}
