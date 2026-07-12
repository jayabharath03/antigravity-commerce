package com.antigravity.commerce.repository;

import com.antigravity.commerce.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, UUID> {
    Optional<AttributeValue> findByAttributeIdAndValue(UUID attributeId, String value);
}
