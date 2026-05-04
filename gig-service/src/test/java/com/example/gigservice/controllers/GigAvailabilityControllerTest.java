package com.example.gigservice.controllers;

import com.example.gigservice.dtos.AvailabilityRequest;
import com.example.gigservice.dtos.GigAvailabilityDto;
import com.example.gigservice.services.GigAvailabilityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GigAvailabilityController.class)
public class GigAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GigAvailabilityService availabilityService;

    @Test
    @DisplayName("POST /gigs/{gigId}/availabilities - Should return 200 OK")
    void addAvailability_Returns200() throws Exception {
        var gigId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        var request = new AvailabilityRequest();
        request.setDays(List.of("MONDAY", "TUESDAY"));
        request.setStartTime("09:00");
        request.setEndTime("17:00");

        var responseDto = new GigAvailabilityDto();
        responseDto.setGigId(gigId);
        responseDto.setAvailableDay("MONDAY");
        responseDto.setStartTime(LocalTime.of(9, 0));
        responseDto.setEndTime(LocalTime.of(17, 0));

        when(availabilityService.addAvailability(eq(gigId), any(AvailabilityRequest.class), eq(userId)))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(post("/gigs/{gigId}/availabilities", gigId)
                .header("X-user-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].availableDay").value("MONDAY"));
    }

    @Test
    @DisplayName("GET /gigs/{gigId}/availabilities - Should return 200 OK")
    void getAvailabilitiesByGigId_Returns200() throws Exception {
        var gigId = UUID.randomUUID();
        var responseDto = new GigAvailabilityDto();
        responseDto.setAvailableDay("MONDAY");

        when(availabilityService.getAvailabilitiesByGigId(gigId)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/gigs/{gigId}/availabilities", gigId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].availableDay").value("MONDAY"));
    }

    @Test
    @DisplayName("DELETE /gigs/availabilities/{availabilityId} - Should return 204 No Content")
    void deleteAvailability_Returns204() throws Exception {
        var availabilityId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        doNothing().when(availabilityService).deleteAvailability(availabilityId, userId);

        mockMvc.perform(delete("/gigs/availabilities/{availabilityId}", availabilityId)
                .header("X-user-Id", userId.toString()))
                .andExpect(status().isNoContent());
    }
}
