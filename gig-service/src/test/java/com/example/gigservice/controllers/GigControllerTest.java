package com.example.gigservice.controllers;

import com.example.gigservice.dtos.*;
import com.example.gigservice.services.GigService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GigController.class)
public class GigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GigService gigService;

    @Test
    @DisplayName("POST /gigs - Should return 201 Created")
    void createGig_Returns201() throws Exception {
        var userId = UUID.randomUUID();
        var gigId = UUID.randomUUID();

        var request = new CreateGigRequest();
        request.setTitle("Plumbing Work");
        request.setDescription("Fixing pipes");
        var pricing = new ServicePricingDto();
        pricing.setBasePrice(new BigDecimal("100.0"));
        request.setPricing(pricing);
        var location = new PointDto();
        location.setLat(6.9271);
        location.setLng(79.8612);
        request.setLocation(location);
        request.setCategoryId(UUID.randomUUID());

        var responseDto = new GigDto();
        responseDto.setId(gigId);
        responseDto.setTitle("Plumbing Work");

        when(gigService.createGig(any(CreateGigRequest.class), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(post("/gigs")
                .header("X-user-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/gigs/" + gigId))
                .andExpect(jsonPath("$.id").value(gigId.toString()))
                .andExpect(jsonPath("$.title").value("Plumbing Work"));
    }

    @Test
    @DisplayName("GET /gigs/search - Should return 200 OK with list")
    void searchNearbyGigs_Returns200() throws Exception {
        var gigDto = new GigDto();
        gigDto.setTitle("Nearby Plumbing");

        when(gigService.searchNearbyGigs(eq(6.9271), eq(79.8612), eq(10.0), any(), any(), any()))
                .thenReturn(List.of(gigDto));

        mockMvc.perform(get("/gigs/search")
                .param("lat", "6.9271")
                .param("lng", "79.8612")
                .param("radiusInKm", "10.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Nearby Plumbing"));
    }

    @Test
    @DisplayName("GET /gigs/{id} - Should return 200 OK")
    void getGigById_Returns200() throws Exception {
        var gigId = UUID.randomUUID();

        var responseDto = new GigDto();
        responseDto.setId(gigId);
        responseDto.setTitle("Plumbing Work");

        when(gigService.getGigById(gigId)).thenReturn(responseDto);

        mockMvc.perform(get("/gigs/{id}", gigId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gigId.toString()))
                .andExpect(jsonPath("$.title").value("Plumbing Work"));
    }

    @Test
    @DisplayName("GET /gigs - Should return 200 OK with list")
    void getAllGigs_Returns200() throws Exception {
        var gigDto = new GigDto();
        gigDto.setTitle("Plumbing Work");

        when(gigService.getAllGigs()).thenReturn(List.of(gigDto));

        mockMvc.perform(get("/gigs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Plumbing Work"));
    }

    @Test
    @DisplayName("PUT /gigs/{id} - Should return 200 OK")
    void updateGig_Returns200() throws Exception {
        var gigId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        var request = new UpdateGigRequest();
        request.setTitle("Updated Title");

        var responseDto = new GigDto();
        responseDto.setId(gigId);
        responseDto.setTitle("Updated Title");

        when(gigService.updateGig(eq(gigId), any(UpdateGigRequest.class), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(put("/gigs/{id}", gigId)
                .header("X-user-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @DisplayName("DELETE /gigs/{id} - Should return 204 No Content")
    void deleteGig_Returns204() throws Exception {
        var gigId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        doNothing().when(gigService).deleteGig(gigId, userId);

        mockMvc.perform(delete("/gigs/{id}", gigId)
                .header("X-user-Id", userId.toString()))
                .andExpect(status().isNoContent());
    }
}
