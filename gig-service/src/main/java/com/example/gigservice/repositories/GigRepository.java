package com.example.gigservice.repositories;

import com.example.gigservice.entities.Gig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GigRepository extends JpaRepository<Gig, UUID> {
}
