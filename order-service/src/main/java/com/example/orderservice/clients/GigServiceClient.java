package com.example.orderservice.clients;

import com.example.orderservice.dtos.GigDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "gig-service", url = "${services.gig-service.url}")
public interface GigServiceClient {

    @GetMapping("/gigs/{id}")
    GigDto getGigById(@PathVariable("id") UUID id);

    @GetMapping("/gigs/{id}/calculate-visit-fee")
    BigDecimal calculateVisitFee(
            @PathVariable("id") UUID id,
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng
    );

    @PutMapping("/gigs/{id}/availability")
    GigDto updateAvailability(
            @PathVariable("id") UUID id,
            @RequestParam("available") boolean available
    );
}
