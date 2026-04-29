package com.example.gigservice.repositories;

import com.example.gigservice.entities.Gig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GigRepository extends JpaRepository<Gig, UUID> {

    @Query(value = "SELECT * FROM gigs g WHERE " +
            "ST_DWithin(g.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, :radius) = true " +
            "AND (cast(:categoryId as uuid) IS NULL OR g.category_id = :categoryId) " +
            "AND (:minPrice IS NULL OR g.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR g.price <= :maxPrice)",
            nativeQuery = true)
    List<Gig> searchNearby(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius,
            @Param("categoryId") UUID categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );
}
