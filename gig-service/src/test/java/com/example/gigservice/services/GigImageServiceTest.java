package com.example.gigservice.services;

import com.example.gigservice.dtos.CreateGigImageRequest;
import com.example.gigservice.dtos.GigImageDto;
import com.example.gigservice.entities.Gig;
import com.example.gigservice.entities.GigImage;
import com.example.gigservice.exceptions.ResourceNotFoundException;
import com.example.gigservice.exceptions.UnauthorizedException;
import com.example.gigservice.mappers.GigImageMapper;
import com.example.gigservice.repositories.GigImageRepository;
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
public class GigImageServiceTest {

    @Mock
    private GigImageRepository gigImageRepository;

    @Mock
    private GigRepository gigRepository;

    @Mock
    private GigImageMapper gigImageMapper;

    @InjectMocks
    private GigImageService gigImageService;

    @Test
    @DisplayName("Should add image to gig successfully")
    void addImageToGig_Success() {
        var gigId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var request = new CreateGigImageRequest();
        request.setImageUrl("http://image.com/1");

        var gig = new Gig();
        gig.setId(gigId);
        gig.setProviderId(userId);

        var savedImage = new GigImage();
        savedImage.setImageUrl("http://image.com/1");
        
        var expectedDto = new GigImageDto();
        expectedDto.setImageUrl("http://image.com/1");

        when(gigRepository.findById(gigId)).thenReturn(Optional.of(gig));
        when(gigImageRepository.save(any(GigImage.class))).thenReturn(savedImage);
        when(gigImageMapper.toDto(any(GigImage.class))).thenReturn(expectedDto);

        var result = gigImageService.addImageToGig(gigId, request, userId);

        assertNotNull(result);
        assertEquals("http://image.com/1", result.getImageUrl());
        verify(gigImageRepository).save(any(GigImage.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when gig not found")
    void addImageToGig_GigNotFound() {
        var gigId = UUID.randomUUID();
        when(gigRepository.findById(gigId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> gigImageService.addImageToGig(gigId, new CreateGigImageRequest(), UUID.randomUUID()));
        verify(gigImageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when wrong user adds image")
    void addImageToGig_Unauthorized() {
        var gigId = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var wrongUserId = UUID.randomUUID();
        
        var gig = new Gig();
        gig.setProviderId(ownerId);

        when(gigRepository.findById(gigId)).thenReturn(Optional.of(gig));

        assertThrows(UnauthorizedException.class, 
                () -> gigImageService.addImageToGig(gigId, new CreateGigImageRequest(), wrongUserId));
        verify(gigImageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return images by gig ID")
    void getImagesByGigId_Success() {
        var gigId = UUID.randomUUID();
        when(gigImageRepository.findByGigId(gigId)).thenReturn(List.of(new GigImage(), new GigImage()));
        when(gigImageMapper.toDto(any(GigImage.class))).thenReturn(new GigImageDto());

        var result = gigImageService.getImagesByGigId(gigId);

        assertEquals(2, result.size());
        verify(gigImageRepository).findByGigId(gigId);
    }

    @Test
    @DisplayName("Should delete gig image successfully")
    void deleteGigImage_Success() {
        var imageId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        
        var gig = new Gig();
        gig.setProviderId(userId);
        
        var gigImage = new GigImage();
        gigImage.setGig(gig);

        when(gigImageRepository.findById(imageId)).thenReturn(Optional.of(gigImage));

        gigImageService.deleteGigImage(imageId, userId);

        verify(gigImageRepository).delete(gigImage);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when gig image not found")
    void deleteGigImage_NotFound() {
        var imageId = UUID.randomUUID();
        when(gigImageRepository.findById(imageId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> gigImageService.deleteGigImage(imageId, UUID.randomUUID()));
        verify(gigImageRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when wrong user deletes image")
    void deleteGigImage_Unauthorized() {
        var imageId = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var wrongUserId = UUID.randomUUID();

        var gig = new Gig();
        gig.setProviderId(ownerId);
        
        var gigImage = new GigImage();
        gigImage.setGig(gig);

        when(gigImageRepository.findById(imageId)).thenReturn(Optional.of(gigImage));

        assertThrows(UnauthorizedException.class, 
                () -> gigImageService.deleteGigImage(imageId, wrongUserId));
        verify(gigImageRepository, never()).delete(any());
    }
}
