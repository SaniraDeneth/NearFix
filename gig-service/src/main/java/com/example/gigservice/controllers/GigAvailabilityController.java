package com.example.gigservice.controllers;

import com.example.gigservice.dtos.AvailabilityRequest;
import com.example.gigservice.dtos.GigAvailabilityDto;
import com.example.gigservice.services.GigAvailabilityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gigs")
@AllArgsConstructor
public class GigAvailabilityController {
    private final GigAvailabilityService availabilityService;

    @PostMapping("/{gigId}/availabilities")
    public ResponseEntity<List<GigAvailabilityDto>> addAvailability(
            @PathVariable UUID gigId,
            @RequestBody @Valid AvailabilityRequest request,
            @RequestHeader("X-user-Id") UUID userId
    ) {
        return ResponseEntity.ok(availabilityService.addAvailability(gigId, request, userId));
    }

    @GetMapping("/{gigId}/availabilities")
    public ResponseEntity<List<GigAvailabilityDto>> getAvailabilitiesByGigId(@PathVariable UUID gigId) {
        return ResponseEntity.ok(availabilityService.getAvailabilitiesByGigId(gigId));
    }

    @DeleteMapping("/availabilities/{availabilityId}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable UUID availabilityId,
            @RequestHeader("X-user-Id") UUID userId
    ) {
        availabilityService.deleteAvailability(availabilityId, userId);
        return ResponseEntity.noContent().build();
    }
}
