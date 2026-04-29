package com.example.gigservice.controllers;

import com.example.gigservice.dtos.CreateGigRequest;
import com.example.gigservice.dtos.UpdateGigRequest;
import com.example.gigservice.dtos.GigDto;
import com.example.gigservice.services.GigService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gigs")
@AllArgsConstructor
public class GigController {
    private final GigService gigService;

    @PostMapping
    public ResponseEntity<GigDto> createGig(
            @RequestBody @Valid CreateGigRequest request,
            @RequestHeader("X-user-Id") UUID userId,
            UriComponentsBuilder uriBuilder
    ) {
        var gigDto = gigService.createGig(request, userId);
        var uri = uriBuilder.path("/gigs/{id}").buildAndExpand(gigDto.getId()).toUri();
        return ResponseEntity.created(uri).body(gigDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GigDto> getGigById(@PathVariable UUID id) {
        return ResponseEntity.ok(gigService.getGigById(id));
    }

    @GetMapping
    public ResponseEntity<List<GigDto>> getAllGigs() {
        return ResponseEntity.ok(gigService.getAllGigs());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GigDto> updateGig(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateGigRequest request,
            @RequestHeader("X-user-Id") UUID userId
    ) {
        return ResponseEntity.ok(gigService.updateGig(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGig(
            @PathVariable UUID id,
            @RequestHeader("X-user-Id") UUID userId
    ) {
        gigService.deleteGig(id, userId);
        return ResponseEntity.noContent().build();
    }
}
