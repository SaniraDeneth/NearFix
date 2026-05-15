package com.example.orderservice.dtos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServicePricingDto {
    private BigDecimal basePrice;
    private BigDecimal travelFeePerKm;
    private String priceType;
    private Integer maxVisitRadiusKm;
}
