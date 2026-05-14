package com.example.gigservice.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class GigDto {
    private UUID id;
    private String title;
    private List<com.example.gigservice.entities.enums.ServiceMode> modes;
    private ServicePricingDto pricing;
    private CategoryDto category;
    private PointDto location;
    private List<String> imageUrls;
    private List<GigAvailabilityDto> availabilities;
}