package com.example.orderservice.repositories;

import com.example.orderservice.entities.Order;
import com.example.orderservice.entities.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    boolean existsByClientIdAndCategoryIdAndStatus(UUID clientId, UUID categoryId, OrderStatus status);
    List<Order> findByClientId(UUID clientId);
    List<Order> findByProviderId(UUID providerId);
}
