package com.example.gigservice.controllers;

import com.example.gigservice.dtos.CreateGigRequest;
import com.example.gigservice.dtos.GigDto;
import com.example.gigservice.services.GigService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/gigs")
@AllArgsConstructor
public class GigController {
    private final GigService gigService;

    @PostMapping
    public ResponseEntity<GigDto> createGig(
            @RequestBody @Valid CreateGigRequest request,
            @RequestHeader("X-user-Id") UUID userId
    ) {
        return  ResponseEntity.ok(gigService.createGig(request, userId)) ;
    }
}
