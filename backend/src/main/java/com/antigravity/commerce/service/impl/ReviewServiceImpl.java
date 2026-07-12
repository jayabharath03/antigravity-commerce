package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.ReviewDto;
import com.antigravity.commerce.entity.Product;
import com.antigravity.commerce.entity.Review;
import com.antigravity.commerce.entity.User;
import com.antigravity.commerce.exception.BadRequestException;
import com.antigravity.commerce.exception.ResourceNotFoundException;
import com.antigravity.commerce.repository.ProductRepository;
import com.antigravity.commerce.repository.ReviewRepository;
import com.antigravity.commerce.repository.UserRepository;
import com.antigravity.commerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public Page<ReviewDto> getReviewsByProduct(UUID productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public ReviewDto createReview(UUID productId, Integer rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(rating)
                .comment(comment)
                .build();

        review = reviewRepository.save(review);
        return toDto(review);
    }

    private ReviewDto toDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId().toString())
                .rating(review.getRating())
                .comment(review.getComment())
                .customerName(review.getUser().getFirstName() + " " + review.getUser().getLastName().charAt(0) + ".")
                .createdAt(review.getCreatedAt())
                .build();
    }
}
