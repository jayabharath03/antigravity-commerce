package com.antigravity.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandDto {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String logoUrl;
    private String website;
    private String country;
    private Boolean active;
}
