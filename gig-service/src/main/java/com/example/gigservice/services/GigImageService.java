package com.example.gigservice.services;

import com.example.gigservice.mappers.GigImageMapper;
import com.example.gigservice.repositories.GigImageRepository;
import com.example.gigservice.repositories.GigRepository;
import com.example.gigservice.dtos.CreateGigImageRequest;
import com.example.gigservice.dtos.GigImageDto;
import com.example.gigservice.entities.GigImage;
import com.example.gigservice.entities.Gig;
import com.example.gigservice.exceptions.ResourceNotFoundException;
import com.example.gigservice.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GigImageService {
    private final GigImageMapper gigImageMapper;
    private final GigImageRepository gigImageRepository;
    private final GigRepository gigRepository;

    public GigImageDto addImageToGig(UUID gigId, CreateGigImageRequest request, UUID userId) {
        Gig gig = gigRepository.findById(gigId)
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found"));

        if (!gig.getProviderId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized to add images to this gig");
        }

        GigImage gigImage = new GigImage();
        gigImage.setGig(gig);
        gigImage.setImageUrl(request.getImageUrl());

        gigImageRepository.save(gigImage);
        return gigImageMapper.toDto(gigImage);
    }

    public List<GigImageDto> getImagesByGigId(UUID gigId) {
        return gigImageRepository.findByGigId(gigId).stream()
                .map(gigImageMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteGigImage(UUID imageId, UUID userId) {
        GigImage gigImage = gigImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Gig image not found"));

        if (!gigImage.getGig().getProviderId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized to delete images from this gig");
        }

        gigImageRepository.delete(gigImage);
    }
}
