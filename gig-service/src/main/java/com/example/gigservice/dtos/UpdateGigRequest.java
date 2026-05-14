package com.example.gigservice.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateGigRequest {
    private String title;
    private String description;
    private UUID categoryId;
    private double lat;
    private double lng;
}
