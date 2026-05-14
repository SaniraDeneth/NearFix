package com.example.gigservice.dtos;

import com.example.gigservice.entities.enums.ServiceMode;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateGigRequest {
    // Step 1
    private String title;
    private String description;
    private UUID categoryId;

    // Step 2
    private List<ServiceMode> modes;

    // Step 3
    private ServicePricingDto pricing;

    // Step 4
    private PointDto location; // Used if VISIT_CLIENT or VISIT_PROVIDER

    // Step 5
    private List<AvailabilityRequest> availabilities;

    // Step 6
    private List<String> imageUrls;
}
