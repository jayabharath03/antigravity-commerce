package com.antigravity.commerce.controller;

import com.antigravity.commerce.dto.ReviewDto;
import com.antigravity.commerce.service.ReviewService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ReviewDto>> getReviews(
            @PathVariable UUID productId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable UUID productId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(productId, request.getRating(), request.getComment()));
    }

    @Data
    public static class ReviewRequest {
        @NotNull
        @Min(1)
        @Max(5)
        private Integer rating;
        
        private String comment;
    }
}
