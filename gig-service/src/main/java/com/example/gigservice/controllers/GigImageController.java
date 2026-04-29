package com.example.gigservice.controllers;

import com.example.gigservice.dtos.CreateGigImageRequest;
import com.example.gigservice.dtos.GigImageDto;
import com.example.gigservice.services.GigImageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gigs")
@AllArgsConstructor
public class GigImageController {
    private final GigImageService gigImageService;

    @PostMapping("/{gigId}/images")
    public ResponseEntity<GigImageDto> addImageToGig(
            @PathVariable UUID gigId,
            @RequestBody @Valid CreateGigImageRequest request,
            @RequestHeader("X-user-Id") UUID userId
    ) {
        return ResponseEntity.ok(gigImageService.addImageToGig(gigId, request, userId));
    }

    @GetMapping("/{gigId}/images")
    public ResponseEntity<List<GigImageDto>> getImagesByGigId(@PathVariable UUID gigId) {
        return ResponseEntity.ok(gigImageService.getImagesByGigId(gigId));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteGigImage(
            @PathVariable UUID imageId,
            @RequestHeader("X-user-Id") UUID userId
    ) {
        gigImageService.deleteGigImage(imageId, userId);
        return ResponseEntity.noContent().build();
    }
}
