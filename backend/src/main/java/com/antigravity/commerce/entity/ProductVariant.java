package com.antigravity.commerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted_at IS NULL") // Automatic soft-delete filter
public class ProductVariant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true)
    private String sku;

    private String barcode;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(name = "low_stock_threshold")
    @Builder.Default
    private Integer lowStockThreshold = 5;

    @Column(name = "allow_backorder")
    @Builder.Default
    private Boolean allowBackorder = false;

    @Column(name = "min_order_quantity")
    @Builder.Default
    private Integer minOrderQuantity = 1;

    @Column(name = "max_order_quantity")
    private Integer maxOrderQuantity;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "variant_attribute_values",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_value_id")
    )
    @Builder.Default
    private Set<AttributeValue> attributeValues = new HashSet<>();

    @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<ProductImage> variantImages = new HashSet<>();
}
