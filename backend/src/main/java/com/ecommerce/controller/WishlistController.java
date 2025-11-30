package com.ecommerce.controller;

import com.ecommerce.dto.response.ApiResponse;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import com.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class WishlistController {
    private final WishlistService wishlistService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getWishlist() {
        User currentUser = userService.getCurrentUser();
        List<ProductResponse> wishlistItems = wishlistService.getWishlist(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Wishlist retrieved successfully", wishlistItems));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addToWishlist(@RequestBody AddToWishlistRequest request) {
        User currentUser = userService.getCurrentUser();
        wishlistService.addToWishlist(currentUser, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Product added to wishlist", "success"));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<String>> removeFromWishlist(@PathVariable String productId) {
        User currentUser = userService.getCurrentUser();
        wishlistService.removeFromWishlist(currentUser, productId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist", "success"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearWishlist() {
        User currentUser = userService.getCurrentUser();
        wishlistService.clearWishlist(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Wishlist cleared", "success"));
    }

    @PostMapping("/{productId}/move-to-cart")
    public ResponseEntity<ApiResponse<String>> moveToCart(@PathVariable String productId) {
        User currentUser = userService.getCurrentUser();
        wishlistService.moveToCart(currentUser, productId);
        return ResponseEntity.ok(ApiResponse.success("Product moved to cart", "success"));
    }

    // Request DTO
    public static class AddToWishlistRequest {
        private String productId;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }
    }
}
