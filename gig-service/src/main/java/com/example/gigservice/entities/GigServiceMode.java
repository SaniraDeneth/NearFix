package com.example.gigservice.entities;

import com.example.gigservice.entities.enums.ServiceMode;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "gig_service_modes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GigServiceMode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gig_id", nullable = false)
    private Gig gig;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 50)
    private ServiceMode mode;
}
