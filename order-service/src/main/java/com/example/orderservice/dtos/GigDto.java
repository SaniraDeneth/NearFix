package com.example.orderservice.dtos;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class GigDto {
    private UUID id;
    private String title;
    private List<String> modes;
    private ServicePricingDto pricing;
    private UUID providerId;
    private CategoryDto category;
    private boolean isAvailable;
}
