package com.antigravity.commerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "attribute_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    // Mapped to attribute_value because 'value' is a reserved SQL keyword in H2 database
    @Column(name = "attribute_value", nullable = false)
    private String value;
}
