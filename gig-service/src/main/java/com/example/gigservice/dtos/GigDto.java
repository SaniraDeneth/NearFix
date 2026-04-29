package com.example.gigservice.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class GigDto {
    private UUID id;
    private String title;
    private double price;
    private CategoryDto category;
    private PointDto location;
    private List<String> imageUrls;
}