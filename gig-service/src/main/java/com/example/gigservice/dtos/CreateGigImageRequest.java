package com.example.gigservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateGigImageRequest {
    @NotBlank(message = "Image URL is required")
    private String imageUrl;
}
