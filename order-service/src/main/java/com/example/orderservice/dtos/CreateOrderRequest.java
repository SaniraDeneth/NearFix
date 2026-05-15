package com.example.orderservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class CreateOrderRequest {
    @NotNull(message = "Gig ID is required")
    private UUID gigId;

    @NotNull(message = "Service mode is required")
    private String serviceMode; // ONLINE, VISIT_CLIENT, VISIT_PROVIDER

    private String clientAddress;
    private Double clientLatitude;
    private Double clientLongitude;
}
