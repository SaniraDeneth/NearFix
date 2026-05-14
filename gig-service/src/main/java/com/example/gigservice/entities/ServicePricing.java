package com.example.gigservice.entities;

import com.example.gigservice.entities.enums.PriceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "service_pricing")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicePricing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gig_id", nullable = false, unique = true)
    private Gig gig;

    @Column(name = "base_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "travel_fee_per_km", precision = 19, scale = 2)
    private BigDecimal travelFeePerKm;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_type", length = 50)
    private PriceType priceType;

    @Column(name = "max_visit_radius_km")
    private Integer maxVisitRadiusKm;
}
