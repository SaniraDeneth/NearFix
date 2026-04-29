package com.example.gigservice.dtos;

import lombok.Data;
import java.util.UUID;

@Data
public class CategoryDto {
    private UUID id;
    private String name;
    private UUID parentId;
}
