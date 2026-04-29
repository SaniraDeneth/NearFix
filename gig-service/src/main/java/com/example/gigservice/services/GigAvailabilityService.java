package com.example.gigservice.services;

import com.example.gigservice.mappers.GigAvailabilityMapper;
import com.example.gigservice.repositories.GigAvailabilityRepository;
import com.example.gigservice.repositories.GigRepository;
import com.example.gigservice.dtos.AvailabilityRequest;
import com.example.gigservice.dtos.GigAvailabilityDto;
import com.example.gigservice.entities.GigAvailability;
import com.example.gigservice.entities.Gig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GigAvailabilityService {
    private final GigAvailabilityMapper availabilityMapper;
    private final GigAvailabilityRepository availabilityRepository;
    private final GigRepository gigRepository;

    public List<GigAvailabilityDto> addAvailability(UUID gigId, AvailabilityRequest request, UUID userId) {
        Gig gig = gigRepository.findById(gigId)
                .orElseThrow(() -> new RuntimeException("Gig not found"));

        if (!gig.getProviderId().equals(userId)) {
            throw new RuntimeException("Unauthorized to add availability to this gig");
        }

        LocalTime start = LocalTime.parse(request.getStartTime());
        LocalTime end = LocalTime.parse(request.getEndTime());
        List<GigAvailability> savedList = new ArrayList<>();

        for (String day : request.getDays()) {
            GigAvailability availability = new GigAvailability();
            availability.setGig(gig);
            availability.setAvailableDay(day);
            availability.setStartTime(start);
            availability.setEndTime(end);
            savedList.add(availabilityRepository.save(availability));
        }

        return savedList.stream()
                .map(availabilityMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<GigAvailabilityDto> getAvailabilitiesByGigId(UUID gigId) {
        return availabilityRepository.findByGigId(gigId).stream()
                .map(availabilityMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteAvailability(UUID availabilityId, UUID userId) {
        GigAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Gig availability not found"));

        if (!availability.getGig().getProviderId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete availability from this gig");
        }

        availabilityRepository.delete(availability);
    }
}
