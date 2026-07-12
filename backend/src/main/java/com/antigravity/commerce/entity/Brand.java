package com.antigravity.commerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted_at IS NULL") // Automatic soft-delete filter
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    private String website;

    private String country;

    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Product> products = new HashSet<>();
}
