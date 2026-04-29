package com.example.gigservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateGigRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotNull(message = "Price is required")
    private double price;
    @NotNull(message = "Provider ID is required")
    private UUID categoryId;
    @NotNull(message = "Location is required")
    private double lat;
    @NotNull(message = "Location is required")
    private double lng;


}

