package com.ecommerce.repository;

import com.ecommerce.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Page<Order> findByUserId(String userId, Pageable pageable);
    
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    long countByStatus(Order.OrderStatus status);
    
    // Find delivered orders for a specific user
    List<Order> findByUserIdAndStatus(String userId, Order.OrderStatus status);
}
