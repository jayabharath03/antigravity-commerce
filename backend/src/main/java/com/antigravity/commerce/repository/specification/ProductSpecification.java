package com.antigravity.commerce.repository.specification;

import com.antigravity.commerce.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import com.antigravity.commerce.entity.ProductVariant;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filterProducts(String search, String categorySlug, String brandSlug, Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Active products only
            predicates.add(criteriaBuilder.equal(root.get("status"), com.antigravity.commerce.entity.ProductStatus.ACTIVE));

            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                predicates.add(
                    criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("shortDescription")), searchPattern)
                    )
                );
            }

            if (StringUtils.hasText(categorySlug)) {
                predicates.add(criteriaBuilder.equal(root.join("category").get("slug"), categorySlug));
            }

            if (StringUtils.hasText(brandSlug)) {
                predicates.add(criteriaBuilder.equal(root.join("brand").get("slug"), brandSlug));
            }

            if (minPrice != null || maxPrice != null) {
                Join<Product, ProductVariant> variantsJoin = root.join("variants");
                if (minPrice != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(variantsJoin.get("price"), BigDecimal.valueOf(minPrice)));
                }
                if (maxPrice != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(variantsJoin.get("price"), BigDecimal.valueOf(maxPrice)));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
