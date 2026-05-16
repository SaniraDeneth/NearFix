package com.example.gigservice.services;

import com.example.gigservice.dtos.*;
import com.example.gigservice.entities.*;
import com.example.gigservice.mappers.GigMapper;
import com.example.gigservice.repositories.GigRepository;
import com.example.gigservice.repositories.CategoryRepository;
import com.example.gigservice.exceptions.ResourceNotFoundException;
import com.example.gigservice.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.example.gigservice.clients.AuthServiceClient;

@Service
@RequiredArgsConstructor
public class GigService {
    private final GigMapper gigMapper;
    private final GigRepository gigRepository;
    private final CategoryRepository categoryRepository;
    private final AuthServiceClient authServiceClient;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Value("${services.internal-secret}")
    private String internalSecret;

    public GigDto createGig(CreateGigRequest request, UUID userId) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        Point location = null;
        if (request.getLocation() != null && (request.getLocation().getLat() != 0 ||  request.getLocation().getLng() != 0)) {
            location = geometryFactory.createPoint(new Coordinate(request.getLocation().getLng(), request.getLocation().getLat()));
        }

        var gig = Gig.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .providerId(userId)
                .category(category)
                .location(location)
                .build();

        if(request.getModes() != null) {
            List<GigServiceMode> modes = request.getModes().stream()
                    .map(mode -> {
                                var gigServiceMode = new GigServiceMode();
                                gigServiceMode.setGig(gig);
                                gigServiceMode.setMode(mode);
                                return gigServiceMode;
                            })
                    .collect(Collectors.toList());
            gig.setModes(modes);
        }

        if(request.getPricing() != null) {
            var priceDto = request.getPricing();
            var pricing = ServicePricing.builder()
                    .gig(gig)
                    .basePrice(priceDto.getBasePrice())
                    .travelFeePerKm(priceDto.getTravelFeePerKm())
                    .priceType(priceDto.getPriceType())
                    .maxVisitRadiusKm(priceDto.getMaxVisitRadiusKm())
                    .build();
            gig.setPricing(pricing);
        }

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

        if (request.getAvailabilities() != null) {
            List<GigAvailability> availabilities = new ArrayList<>();
            for (AvailabilityRequest availReq : request.getAvailabilities()) {
                LocalTime start = LocalTime.parse(availReq.getStartTime());
                LocalTime end = LocalTime.parse(availReq.getEndTime());
                for (String day : availReq.getDays()) {
                    GigAvailability availability = new GigAvailability();
                    availability.setGig(gig);
                    availability.setAvailableDay(day);
                    availability.setStartTime(start);
                    availability.setEndTime(end);
                    availabilities.add(availability);
                }
            }
            gig.setAvailabilities(availabilities);
        }

        gigRepository.save(gig);
        
        try {
            authServiceClient.upgradeUserToProvider(userId, internalSecret);
        } catch (Exception e) {
            // Log or handle exception if needed, but do not block gig creation if auth-service fails
            System.err.println("Failed to upgrade user " + userId + " to PROVIDER: " + e.getMessage());
        }

        return gigMapper.toDto(gig);
    }

    public GigDto getGigById(UUID id) {
        Gig gig = gigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found"));
        return gigMapper.toDto(gig);
    }

    public List<GigDto> getAllGigs() {
        return gigRepository.findAll().stream()
                .map(gigMapper::toDto)
                .collect(Collectors.toList());
    }

    public GigDto updateGig(UUID id, UpdateGigRequest request, UUID userId) {
        Gig gig = gigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found"));

        if (!gig.getProviderId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized to update this gig");
        }

        if (request.getTitle() != null) gig.setTitle(request.getTitle());
        if (request.getDescription() != null) gig.setDescription(request.getDescription());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
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
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found"));

        if (!gig.getProviderId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized to delete this gig");
        }

        gigRepository.delete(gig);
    }

    public List<GigDto> searchNearbyGigs(double lat, double lng, double radiusInKm, UUID categoryId, Double minPrice, Double maxPrice) {
        double radiusInMeters = radiusInKm * 1000;
        return gigRepository.searchNearby(lat, lng, radiusInMeters, categoryId, minPrice, maxPrice).stream()
                .map(gigMapper::toDto)
                .collect(Collectors.toList());
    }

    public double calculateDistanceInKm(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public java.math.BigDecimal calculateVisitFee(UUID gigId, double clientLat, double clientLng) {
        Gig gig = gigRepository.findById(gigId)
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found"));
        if (gig.getLocation() == null) {
            throw new IllegalArgumentException("Gig does not have a physical location registered");
        }

        double distance = calculateDistanceInKm(
                // Point getY() returns Latitude, getX() returns Longitude
                gig.getLocation().getY(), gig.getLocation().getX(),
                clientLat, clientLng
        );

        ServicePricing pricing = gig.getPricing();
        if (pricing == null) {
            throw new IllegalStateException("Gig does not have pricing details configured");
        }

        if (pricing.getTravelFeePerKm() == null) {
            return java.math.BigDecimal.ZERO;
        }

        return pricing.getTravelFeePerKm().multiply(java.math.BigDecimal.valueOf(distance))
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public GigDto updateAvailability(UUID id, boolean available) {
        Gig gig = gigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found"));
        gig.setAvailable(available);
        gigRepository.save(gig);
        return gigMapper.toDto(gig);
    }
}
