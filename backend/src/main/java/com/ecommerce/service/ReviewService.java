package com.ecommerce.service;

import com.ecommerce.dto.request.ReviewRequest;
import com.ecommerce.dto.response.ReviewResponse;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Order;
import com.ecommerce.model.Review;
import com.ecommerce.model.User;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    
    public Page<ReviewResponse> getProductReviews(String productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(ReviewResponse::fromReview);
    }
    
    public ReviewResponse createReview(ReviewRequest request) {
        User user = userService.getCurrentUser();
        
        // Check if product exists
        if (!productRepository.existsById(request.getProductId())) {
            throw new ResourceNotFoundException("Product", "id", request.getProductId());
        }
        
        // Check if user already reviewed this product
        if (reviewRepository.existsByProductIdAndUserId(request.getProductId(), user.getId())) {
            throw new BadRequestException("You have already reviewed this product");
        }
        
        // Check if user has purchased the product (verified purchase)
        boolean isVerifiedPurchase = hasUserPurchasedProduct(user.getId(), request.getProductId());
        
        Review review = Review.builder()
                .productId(request.getProductId())
                .userId(user.getId())
                .userName(user.getFirstName() + " " + user.getLastName())
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .verified(isVerifiedPurchase)
                .build();
        
        review = reviewRepository.save(review);
        
        // Update product rating
        updateProductRating(request.getProductId());
        
        return ReviewResponse.fromReview(review);
    }
    
    /**
     * Check if a user has purchased a specific product.
     * A purchase is verified if the user has a delivered order containing the product.
     */
    private boolean hasUserPurchasedProduct(String userId, String productId) {
        // Get all delivered orders for this user
        List<Order> deliveredOrders = orderRepository.findByUserIdAndStatus(userId, Order.OrderStatus.DELIVERED);
        
        // Check if any delivered order contains the product
        return deliveredOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .anyMatch(item -> item.getProductId().equals(productId));
    }
    
    public ReviewResponse updateReview(String reviewId, ReviewRequest request) {
        User user = userService.getCurrentUser();
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        if (!review.getUserId().equals(user.getId())) {
            throw new BadRequestException("You can only update your own reviews");
        }
        
        review.setRating(request.getRating());
        if (request.getTitle() != null) review.setTitle(request.getTitle());
        if (request.getComment() != null) review.setComment(request.getComment());
        
        review = reviewRepository.save(review);
        
        // Update product rating
        updateProductRating(review.getProductId());
        
        return ReviewResponse.fromReview(review);
    }
    
    public void deleteReview(String reviewId) {
        User user = userService.getCurrentUser();
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        if (!review.getUserId().equals(user.getId()) && 
            !user.getRoles().contains(User.Role.ADMIN)) {
            throw new BadRequestException("You can only delete your own reviews");
        }
        
        String productId = review.getProductId();
        reviewRepository.delete(review);
        
        // Update product rating
        updateProductRating(productId);
    }
    
    public ReviewResponse markHelpful(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        review = reviewRepository.save(review);
        
        return ReviewResponse.fromReview(review);
    }
    
    private void updateProductRating(String productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        
        if (reviews.isEmpty()) {
            productService.updateProductRating(productId, 0.0, 0);
        } else {
            double averageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            productService.updateProductRating(productId, averageRating, reviews.size());
        }
    }
}
