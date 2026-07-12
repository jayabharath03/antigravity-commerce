package com.antigravity.commerce.service;

import com.antigravity.commerce.dto.ReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ReviewService {
    Page<ReviewDto> getReviewsByProduct(UUID productId, Pageable pageable);
    ReviewDto createReview(UUID productId, Integer rating, String comment);
}
