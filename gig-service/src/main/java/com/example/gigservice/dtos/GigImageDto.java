package com.example.gigservice.dtos;

import lombok.Data;
import java.util.UUID;

@Data
public class GigImageDto {
    private UUID id;
    private UUID gigId;
    private String imageUrl;
}
