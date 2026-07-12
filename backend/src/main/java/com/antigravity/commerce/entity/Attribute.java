package com.antigravity.commerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<AttributeValue> values = new HashSet<>();
}
