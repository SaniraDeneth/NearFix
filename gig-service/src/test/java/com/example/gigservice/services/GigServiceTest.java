package com.example.gigservice.services;

import com.example.gigservice.dtos.AvailabilityRequest;
import com.example.gigservice.dtos.CreateGigRequest;
import com.example.gigservice.dtos.GigDto;
import com.example.gigservice.dtos.UpdateGigRequest;
import com.example.gigservice.entities.Category;
import com.example.gigservice.entities.Gig;
import com.example.gigservice.exceptions.ResourceNotFoundException;
import com.example.gigservice.exceptions.UnauthorizedException;
import com.example.gigservice.mappers.GigMapper;
import com.example.gigservice.repositories.CategoryRepository;
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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GigServiceTest {

    @Mock
    private GigRepository gigRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private GigMapper gigMapper;

    @InjectMocks
    private GigService gigService;

    @Test
    @DisplayName("Should create gig successfully")
    void createGig_Success() {
        var userId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        
        var request = new CreateGigRequest();
        request.setTitle("Fix Plumbing");
        request.setDescription("Fixing leaks");
        request.setPrice(50.0);
        request.setCategoryId(categoryId);
        request.setLat(6.9271);
        request.setLng(79.8612);
        request.setImageUrls(List.of("http://image1.com"));
        
        var availReq = new AvailabilityRequest();
        availReq.setStartTime("09:00:00");
        availReq.setEndTime("17:00:00");
        availReq.setDays(List.of("MONDAY"));
        request.setAvailabilities(List.of(availReq));

        var category = new Category();
        category.setId(categoryId);

        var savedGig = new Gig();
        savedGig.setTitle("Fix Plumbing");

        var expectedDto = new GigDto();
        expectedDto.setTitle("Fix Plumbing");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(gigRepository.save(any(Gig.class))).thenReturn(savedGig);
        when(gigMapper.toDto(any(Gig.class))).thenReturn(expectedDto);

        var result = gigService.createGig(request, userId);

        assertNotNull(result);
        assertEquals("Fix Plumbing", result.getTitle());
        verify(gigRepository).save(any(Gig.class));
    }

    @Test
    @DisplayName("Should throw exception when creating gig with invalid category")
    void createGig_CategoryNotFound() {
        var userId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        
        var request = new CreateGigRequest();
        request.setCategoryId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gigService.createGig(request, userId));
        verify(gigRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return gig when ID exists")
    void getGigById_Success() {
        var id = UUID.randomUUID();
        var gig = new Gig();
        gig.setTitle("Test Gig");

        var expectedDto = new GigDto();
        expectedDto.setTitle("Test Gig");

        when(gigRepository.findById(id)).thenReturn(Optional.of(gig));
        when(gigMapper.toDto(gig)).thenReturn(expectedDto);

        var result = gigService.getGigById(id);

        assertNotNull(result);
        assertEquals("Test Gig", result.getTitle());
    }

    @Test
    @DisplayName("Should throw exception when gig ID not found")
    void getGigById_NotFound() {
        var id = UUID.randomUUID();
        when(gigRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gigService.getGigById(id));
    }

    @Test
    @DisplayName("Should return list of all gigs")
    void getAllGigs_Success() {
        when(gigRepository.findAll()).thenReturn(List.of(new Gig(), new Gig()));
        when(gigMapper.toDto(any(Gig.class))).thenReturn(new GigDto());

        var result = gigService.getAllGigs();

        assertEquals(2, result.size());
        verify(gigRepository).findAll();
    }

    @Test
    @DisplayName("Should update gig successfully when authorized")
    void updateGig_Success() {
        var id = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();

        var request = new UpdateGigRequest();
        request.setTitle("Updated Title");
        request.setPrice(100.0);
        request.setCategoryId(categoryId);

        var gig = new Gig();
        gig.setProviderId(userId);

        var category = new Category();

        when(gigRepository.findById(id)).thenReturn(Optional.of(gig));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(gigMapper.toDto(any(Gig.class))).thenReturn(new GigDto());

        gigService.updateGig(id, request, userId);

        assertEquals("Updated Title", gig.getTitle());
        assertEquals(100.0, gig.getPrice());
        assertEquals(category, gig.getCategory());
        verify(gigRepository).save(gig);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when wrong user tries to update gig")
    void updateGig_Unauthorized() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var wrongUserId = UUID.randomUUID();

        var gig = new Gig();
        gig.setProviderId(ownerId);

        when(gigRepository.findById(id)).thenReturn(Optional.of(gig));

        assertThrows(UnauthorizedException.class, () -> gigService.updateGig(id, new UpdateGigRequest(), wrongUserId));
        verify(gigRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete gig successfully when authorized")
    void deleteGig_Success() {
        var id = UUID.randomUUID();
        var userId = UUID.randomUUID();

        var gig = new Gig();
        gig.setProviderId(userId);

        when(gigRepository.findById(id)).thenReturn(Optional.of(gig));

        gigService.deleteGig(id, userId);

        verify(gigRepository).delete(gig);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when wrong user tries to delete gig")
    void deleteGig_Unauthorized() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var wrongUserId = UUID.randomUUID();

        var gig = new Gig();
        gig.setProviderId(ownerId);

        when(gigRepository.findById(id)).thenReturn(Optional.of(gig));

        assertThrows(UnauthorizedException.class, () -> gigService.deleteGig(id, wrongUserId));
        verify(gigRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should search nearby gigs with correctly converted radius")
    void searchNearbyGigs_Success() {
        double lat = 6.9;
        double lng = 79.8;
        double radiusKm = 5.0;
        UUID categoryId = UUID.randomUUID();
        
        when(gigRepository.searchNearby(eq(lat), eq(lng), eq(5000.0), eq(categoryId), any(), any()))
                .thenReturn(List.of(new Gig()));
        when(gigMapper.toDto(any(Gig.class))).thenReturn(new GigDto());

        var result = gigService.searchNearbyGigs(lat, lng, radiusKm, categoryId, null, null);

        assertEquals(1, result.size());
        verify(gigRepository).searchNearby(lat, lng, 5000.0, categoryId, null, null);
    }

    @Test
    @DisplayName("Should create gig successfully with minimal fields (nulls)")
    void createGig_MinimalFields_Success() {
        var userId = UUID.randomUUID();
        
        var request = new CreateGigRequest();
        request.setTitle("Minimal Gig");
        
        var savedGig = new Gig();
        savedGig.setTitle("Minimal Gig");

        var expectedDto = new GigDto();
        expectedDto.setTitle("Minimal Gig");

        when(gigRepository.save(any(Gig.class))).thenReturn(savedGig);
        when(gigMapper.toDto(any(Gig.class))).thenReturn(expectedDto);

        var result = gigService.createGig(request, userId);

        assertNotNull(result);
        verify(categoryRepository, never()).findById(any());
        verify(gigRepository).save(any(Gig.class));
    }

    @Test
    @DisplayName("Should update gig partially (only description and location)")
    void updateGig_PartialFields_Success() {
        var id = UUID.randomUUID();
        var userId = UUID.randomUUID();

        var request = new UpdateGigRequest();
        request.setDescription("New Description");
        request.setLat(6.9);
        request.setLng(79.8);

        var gig = new Gig();
        gig.setProviderId(userId);

        when(gigRepository.findById(id)).thenReturn(Optional.of(gig));
        when(gigMapper.toDto(any(Gig.class))).thenReturn(new GigDto());

        gigService.updateGig(id, request, userId);

        assertEquals("New Description", gig.getDescription());
        assertNotNull(gig.getLocation());
        verify(categoryRepository, never()).findById(any());
        verify(gigRepository).save(gig);
    }

    @Test
    @DisplayName("Should throw exception when category to update is not found")
    void updateGig_CategoryNotFound() {
        var id = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();

        var request = new UpdateGigRequest();
        request.setCategoryId(categoryId);

        var gig = new Gig();
        gig.setProviderId(userId);

        when(gigRepository.findById(id)).thenReturn(Optional.of(gig));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gigService.updateGig(id, request, userId));
        verify(gigRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when gig to update is not found")
    void updateGig_NotFound() {
        var id = UUID.randomUUID();
        when(gigRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gigService.updateGig(id, new UpdateGigRequest(), UUID.randomUUID()));
    }
    
    @Test
    @DisplayName("Should throw exception when gig to delete is not found")
    void deleteGig_NotFound() {
        var id = UUID.randomUUID();
        when(gigRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gigService.deleteGig(id, UUID.randomUUID()));
    }
}
