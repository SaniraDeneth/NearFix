package com.example.gigservice.dtos;

import com.example.gigservice.entities.enums.PriceType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicePricingDto {
    private BigDecimal basePrice;
    private BigDecimal travelFeePerKm;
    private PriceType priceType;
    private Integer maxVisitRadiusKm;
}