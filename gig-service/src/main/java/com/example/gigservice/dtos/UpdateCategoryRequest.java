package com.example.gigservice.dtos;

import lombok.Data;
import java.util.UUID;

@Data
public class UpdateCategoryRequest {
    private String name;
    private UUID parentId;
}
