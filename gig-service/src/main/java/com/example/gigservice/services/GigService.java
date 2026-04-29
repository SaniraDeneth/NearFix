package com.example.gigservice.services;

import com.example.gigservice.mappers.GigMapper;
import com.example.gigservice.repositories.GigRepository;
import com.example.gigservice.repositories.CategoryRepository;
import com.example.gigservice.dtos.CreateGigRequest;
import com.example.gigservice.dtos.UpdateGigRequest;
import com.example.gigservice.dtos.GigDto;
import com.example.gigservice.entities.Gig;
import com.example.gigservice.entities.Category;
import com.example.gigservice.entities.GigImage;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GigService {
    private final GigMapper gigMapper;
    private final GigRepository gigRepository;
    private final CategoryRepository categoryRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public GigDto createGig(CreateGigRequest request, UUID userId) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }

        Point location = null;
        if (request.getLat() != 0 || request.getLng() != 0) {
            location = geometryFactory.createPoint(new Coordinate(request.getLng(), request.getLat()));
        }

        var gig = Gig.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .providerId(userId)
                .category(category)
                .location(location)
                .build();

        if (request.getImageUrls() != null) {
            List<GigImage> gigImages = request.getImageUrls().stream()
                    .map(url -> {
                        GigImage img = new GigImage();
                        img.setGig(gig);
                        img.setImageUrl(url);
                        return img;
                    })
                    .collect(Collectors.toList());
            gig.setImages(gigImages);
        }

        gigRepository.save(gig);
        return gigMapper.toDto(gig);
    }

    public GigDto getGigById(UUID id) {
        Gig gig = gigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gig not found"));
        return gigMapper.toDto(gig);
    }

    public List<GigDto> getAllGigs() {
        return gigRepository.findAll().stream()
                .map(gigMapper::toDto)
                .collect(Collectors.toList());
    }

    public GigDto updateGig(UUID id, UpdateGigRequest request, UUID userId) {
        Gig gig = gigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gig not found"));

        if (!gig.getProviderId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this gig");
        }

        if (request.getTitle() != null) gig.setTitle(request.getTitle());
        if (request.getDescription() != null) gig.setDescription(request.getDescription());
        if (request.getPrice() != 0) gig.setPrice(request.getPrice());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            gig.setCategory(category);
        }

        if (request.getLat() != 0 || request.getLng() != 0) {
            Point location = geometryFactory.createPoint(new Coordinate(request.getLng(), request.getLat()));
            gig.setLocation(location);
        }

        gigRepository.save(gig);
        return gigMapper.toDto(gig);
    }

    public void deleteGig(UUID id, UUID userId) {
        Gig gig = gigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gig not found"));

        if (!gig.getProviderId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this gig");
        }

        gigRepository.delete(gig);
    }
}
