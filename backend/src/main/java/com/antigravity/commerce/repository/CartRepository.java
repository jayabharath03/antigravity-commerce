package com.antigravity.commerce.repository;

import com.antigravity.commerce.entity.Cart;
import com.antigravity.commerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findBySessionId(String sessionId);
}
