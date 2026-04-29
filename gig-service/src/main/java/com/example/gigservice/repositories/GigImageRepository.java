package com.example.gigservice.repositories;

import com.example.gigservice.entities.GigImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface GigImageRepository extends JpaRepository<GigImage, UUID> {
    List<GigImage> findByGigId(UUID gigId);
}
