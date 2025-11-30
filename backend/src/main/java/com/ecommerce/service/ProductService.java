package com.ecommerce.service;

import com.ecommerce.dto.request.ProductRequest;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(ProductResponse::fromProduct);
    }
    
    public ProductResponse getProductById(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return ProductResponse.fromProduct(product);
    }
    
    public Page<ProductResponse> getProductsByCategory(String categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable)
                .map(ProductResponse::fromProduct);
    }
    
    public Page<ProductResponse> getFeaturedProducts(Pageable pageable) {
        return productRepository.findByFeaturedTrueAndActiveTrue(pageable)
                .map(ProductResponse::fromProduct);
    }
    
    public Page<ProductResponse> searchProducts(String query, Pageable pageable) {
        return productRepository.searchByName(query, pageable)
                .map(ProductResponse::fromProduct);
    }
    
    public Page<ProductResponse> filterByPrice(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(ProductResponse::fromProduct);
    }
    
    public Page<ProductResponse> filterByRating(double minRating, Pageable pageable) {
        return productRepository.findByMinRating(minRating, pageable)
                .map(ProductResponse::fromProduct);
    }
    
    // Admin methods
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .brand(request.getBrand())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .discountPercentage(request.getDiscountPercentage())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .images(request.getImages())
                .stockQuantity(request.getStockQuantity())
                .active(request.isActive())
                .featured(request.isFeatured())
                .tags(request.getTags())
                .specs(Product.ProductSpecs.builder()
                        .weight(request.getWeight())
                        .dimensions(request.getDimensions())
                        .color(request.getColor())
                        .material(request.getMaterial())
                        .warranty(request.getWarranty())
                        .build())
                .build();
        
        product = productRepository.save(product);
        return ProductResponse.fromProduct(product);
    }
    
    public ProductResponse updateProduct(String productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (request.getCategoryId() != null && !request.getCategoryId().equals(product.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategoryId(category.getId());
            product.setCategoryName(category.getName());
        }
        
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getBrand() != null) product.setBrand(request.getBrand());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getDiscountPrice() != null) product.setDiscountPrice(request.getDiscountPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());
        if (request.getImages() != null) product.setImages(request.getImages());
        product.setStockQuantity(request.getStockQuantity());
        product.setActive(request.isActive());
        product.setFeatured(request.isFeatured());
        if (request.getTags() != null) product.setTags(request.getTags());
        
        if (request.getWeight() != null || request.getDimensions() != null || 
            request.getColor() != null || request.getMaterial() != null || 
            request.getWarranty() != null) {
            
            Product.ProductSpecs specs = product.getSpecs();
            if (specs == null) {
                specs = new Product.ProductSpecs();
            }
            if (request.getWeight() != null) specs.setWeight(request.getWeight());
            if (request.getDimensions() != null) specs.setDimensions(request.getDimensions());
            if (request.getColor() != null) specs.setColor(request.getColor());
            if (request.getMaterial() != null) specs.setMaterial(request.getMaterial());
            if (request.getWarranty() != null) specs.setWarranty(request.getWarranty());
            product.setSpecs(specs);
        }
        
        product = productRepository.save(product);
        return ProductResponse.fromProduct(product);
    }
    
    public void deleteProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        product.setActive(false);
        productRepository.save(product);
    }
    
    public void updateProductRating(String productId, double newAverageRating, int reviewCount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        product.setAverageRating(newAverageRating);
        product.setReviewCount(reviewCount);
        productRepository.save(product);
    }
    
    public Page<ProductResponse> getAllProductsAdmin(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductResponse::fromProduct);
    }
}
