package com.antigravity.commerce.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ReviewDto {
    private String id;
    private Integer rating;
    private String comment;
    private String customerName;
    private LocalDateTime createdAt;
}
