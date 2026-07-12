package com.antigravity.commerce.repository;

import com.antigravity.commerce.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = {"category", "brand"})
    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
