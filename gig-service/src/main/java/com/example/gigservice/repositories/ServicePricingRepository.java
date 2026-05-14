package com.example.gigservice.repositories;

import com.example.gigservice.entities.ServicePricing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServicePricingRepository extends JpaRepository<ServicePricing, UUID> {
}
