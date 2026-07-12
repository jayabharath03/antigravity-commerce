package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.CartDto;
import com.antigravity.commerce.dto.CartRequest;
import com.antigravity.commerce.entity.Cart;
import com.antigravity.commerce.entity.CartItem;
import com.antigravity.commerce.entity.Product;
import com.antigravity.commerce.entity.User;
import com.antigravity.commerce.exception.ResourceNotFoundException;
import com.antigravity.commerce.mapper.CartMapper;
import com.antigravity.commerce.repository.CartRepository;
import com.antigravity.commerce.repository.ProductRepository;
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

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
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
