package com.antigravity.commerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted_at IS NULL") // Automatic soft-delete filter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "long_description")
    private String longDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(name = "seo_title")
    private String seoTitle;

    @Column(name = "seo_description")
    private String seoDescription;

    @Column(name = "tax_class")
    private String taxClass;

    @Column(name = "hsn_code")
    private String hsnCode;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    private String manufacturer;

    @Column(name = "warranty_period")
    private String warrantyPeriod;

    @Builder.Default
    private Boolean returnable = false;

    @Column(name = "return_days")
    @Builder.Default
    private Integer returnDays = 0;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<ProductVariant> variants = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<ProductImage> images = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "product_attributes",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_id")
    )
    @Builder.Default
    private Set<Attribute> attributes = new HashSet<>();
}
