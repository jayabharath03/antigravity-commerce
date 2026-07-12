package com.antigravity.commerce.repository;

import com.antigravity.commerce.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, UUID> {
    Optional<Attribute> findByName(String name);
}
