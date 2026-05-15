package com.example.orderservice.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrderDto {
    private UUID id;
    private UUID clientId;
    private UUID providerId;
    private UUID gigId;
    private UUID categoryId;
    private String serviceMode;
    private BigDecimal basePrice;
    private BigDecimal travelFee;
    private BigDecimal totalPrice;
    private String clientAddress;
    private Double clientLatitude;
    private Double clientLongitude;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
