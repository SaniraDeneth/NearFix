package com.example.gigservice.repositories;

import com.example.gigservice.entities.GigAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GigAvailabilityRepository extends JpaRepository<GigAvailability, UUID> {
    List<GigAvailability> findByGigId(UUID gigId);
}
