package com.example.gigservice.repositories;

import com.example.gigservice.entities.GigServiceMode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GigServiceModeRepository extends JpaRepository<GigServiceMode, UUID> {
}
