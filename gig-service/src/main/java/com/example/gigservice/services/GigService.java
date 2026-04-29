package com.example.gigservice.services;

import com.example.gigservice.mappers.GigMapper;
import com.example.gigservice.repositories.GigRepository;
import com.example.gigservice.dtos.CreateGigRequest;
import com.example.gigservice.dtos.GigDto;
import com.example.gigservice.entities.Gig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class GigService {
    private final GigMapper gigMapper;
    private GigRepository gigRepository;

    public GigDto createGig(CreateGigRequest request, UUID userId) {
        var gig = Gig.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .providerId(userId)
                .build();

        gigRepository.save(gig);
        return gigMapper.toDto(gig);
    }
}
