package com.example.gigservice.dtos;

import lombok.Data;

import java.util.List;

@Data
public class AvailabilityRequest {
    private List<String> days;
    private String startTime;
    private String endTime;
}
