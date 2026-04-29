package com.example.gigservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gigs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Gig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "location", columnDefinition = "GEOGRAPHY(POINT, 4326)")
    private Point location;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
