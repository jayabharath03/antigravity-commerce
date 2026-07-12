package com.antigravity.commerce.repository;

import com.antigravity.commerce.entity.Order;
import com.antigravity.commerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUser(User user, Pageable pageable);
    Optional<Order> findByOrderNumber(String orderNumber);
}
