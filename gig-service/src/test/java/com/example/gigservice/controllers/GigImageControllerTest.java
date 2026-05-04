package com.example.gigservice.controllers;

import com.example.gigservice.dtos.CreateGigImageRequest;
import com.example.gigservice.dtos.GigImageDto;
import com.example.gigservice.services.GigImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GigImageController.class)
public class GigImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GigImageService gigImageService;

    @Test
    @DisplayName("POST /gigs/{gigId}/images - Should return 200 OK")
    void addImageToGig_Returns200() throws Exception {
        var gigId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var imageId = UUID.randomUUID();

        var request = new CreateGigImageRequest();
        request.setImageUrl("http://example.com/image.jpg");

        var responseDto = new GigImageDto();
        responseDto.setId(imageId);
        responseDto.setGigId(gigId);
        responseDto.setImageUrl("http://example.com/image.jpg");

        when(gigImageService.addImageToGig(eq(gigId), any(CreateGigImageRequest.class), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(post("/gigs/{gigId}/images", gigId)
                .header("X-user-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(imageId.toString()))
                .andExpect(jsonPath("$.gigId").value(gigId.toString()))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/image.jpg"));
    }

    @Test
    @DisplayName("GET /gigs/{gigId}/images - Should return 200 OK")
    void getImagesByGigId_Returns200() throws Exception {
        var gigId = UUID.randomUUID();
        var imageDto = new GigImageDto();
        imageDto.setImageUrl("http://example.com/image.jpg");

        when(gigImageService.getImagesByGigId(gigId)).thenReturn(List.of(imageDto));

        mockMvc.perform(get("/gigs/{gigId}/images", gigId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].imageUrl").value("http://example.com/image.jpg"));
    }

    @Test
    @DisplayName("DELETE /gigs/images/{imageId} - Should return 204 No Content")
    void deleteGigImage_Returns204() throws Exception {
        var imageId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        doNothing().when(gigImageService).deleteGigImage(imageId, userId);

        mockMvc.perform(delete("/gigs/images/{imageId}", imageId)
                .header("X-user-Id", userId.toString()))
                .andExpect(status().isNoContent());
    }
}
