package com.example.gigservice.services;

import com.example.gigservice.dtos.AvailabilityRequest;
import com.example.gigservice.dtos.GigAvailabilityDto;
import com.example.gigservice.entities.Gig;
import com.example.gigservice.entities.GigAvailability;
import com.example.gigservice.exceptions.ResourceNotFoundException;
import com.example.gigservice.exceptions.UnauthorizedException;
import com.example.gigservice.mappers.GigAvailabilityMapper;
import com.example.gigservice.repositories.GigAvailabilityRepository;
import com.example.gigservice.repositories.GigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GigAvailabilityServiceTest {

    @Mock
    private GigAvailabilityRepository availabilityRepository;

    @Mock
    private GigRepository gigRepository;

    @Mock
    private GigAvailabilityMapper availabilityMapper;

    @InjectMocks
    private GigAvailabilityService availabilityService;

    @Test
    @DisplayName("Should add availability to gig successfully")
    void addAvailability_Success() {
        var gigId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        
        var request = new AvailabilityRequest();
        request.setStartTime("09:00");
        request.setEndTime("17:00");
        request.setDays(List.of("MONDAY", "TUESDAY"));

        var gig = new Gig();
        gig.setId(gigId);
        gig.setProviderId(userId);

        var savedAvailability = new GigAvailability();
        
        when(gigRepository.findById(gigId)).thenReturn(Optional.of(gig));
        when(availabilityRepository.save(any(GigAvailability.class))).thenReturn(savedAvailability);
        when(availabilityMapper.toDto(any(GigAvailability.class))).thenReturn(new GigAvailabilityDto());

        var result = availabilityService.addAvailability(gigId, request, userId);

        assertEquals(2, result.size());
        verify(availabilityRepository, times(2)).save(any(GigAvailability.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when gig not found")
    void addAvailability_GigNotFound() {
        var gigId = UUID.randomUUID();
        when(gigRepository.findById(gigId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> availabilityService.addAvailability(gigId, new AvailabilityRequest(), UUID.randomUUID()));
        verify(availabilityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when wrong user adds availability")
    void addAvailability_Unauthorized() {
        var gigId = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var wrongUserId = UUID.randomUUID();

        var gig = new Gig();
        gig.setProviderId(ownerId);

        when(gigRepository.findById(gigId)).thenReturn(Optional.of(gig));

        assertThrows(UnauthorizedException.class, 
                () -> availabilityService.addAvailability(gigId, new AvailabilityRequest(), wrongUserId));
        verify(availabilityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return availabilities by gig ID")
    void getAvailabilitiesByGigId_Success() {
        var gigId = UUID.randomUUID();
        when(availabilityRepository.findByGigId(gigId)).thenReturn(List.of(new GigAvailability()));
        when(availabilityMapper.toDto(any(GigAvailability.class))).thenReturn(new GigAvailabilityDto());

        var result = availabilityService.getAvailabilitiesByGigId(gigId);

        assertEquals(1, result.size());
        verify(availabilityRepository).findByGigId(gigId);
    }

    @Test
    @DisplayName("Should delete gig availability successfully")
    void deleteAvailability_Success() {
        var availabilityId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        var gig = new Gig();
        gig.setProviderId(userId);

        var availability = new GigAvailability();
        availability.setGig(gig);

        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(availability));

        availabilityService.deleteAvailability(availabilityId, userId);

        verify(availabilityRepository).delete(availability);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when availability not found")
    void deleteAvailability_NotFound() {
        var availabilityId = UUID.randomUUID();
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> availabilityService.deleteAvailability(availabilityId, UUID.randomUUID()));
        verify(availabilityRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when wrong user deletes availability")
    void deleteAvailability_Unauthorized() {
        var availabilityId = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var wrongUserId = UUID.randomUUID();

        var gig = new Gig();
        gig.setProviderId(ownerId);

        var availability = new GigAvailability();
        availability.setGig(gig);

        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(availability));

        assertThrows(UnauthorizedException.class, 
                () -> availabilityService.deleteAvailability(availabilityId, wrongUserId));
        verify(availabilityRepository, never()).delete(any());
    }
}
