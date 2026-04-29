package com.example.gigservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class CreateCategoryRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private UUID parentId;
}
