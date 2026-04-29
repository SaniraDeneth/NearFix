package com.example.gigservice.dtos;

import lombok.Data;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class GigAvailabilityDto {
    private UUID id;
    private UUID gigId;
    private String availableDay;
    private LocalTime startTime;
    private LocalTime endTime;
}
