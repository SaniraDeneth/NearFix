package com.example.gigservice.controllers;

import com.example.gigservice.dtos.CreateCategoryRequest;
import com.example.gigservice.exceptions.ResourceNotFoundException;
import com.example.gigservice.exceptions.UnauthorizedException;
import com.example.gigservice.services.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class GlobalExceptionHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @DisplayName("Should handle ResourceNotFoundException and return 404")
    void handleResourceNotFound_Returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(categoryService.getCategoryById(id))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(get("/categories/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Category not found"))
                .andExpect(jsonPath("$.path").value("/categories/" + id));
    }

    @Test
    @DisplayName("Should handle UnauthorizedException and return 403")
    void handleUnauthorized_Returns403() throws Exception {
        UUID id = UUID.randomUUID();

        when(categoryService.getCategoryById(id))
                .thenThrow(new UnauthorizedException("You are not authorized"));

        mockMvc.perform(get("/categories/{id}", id))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You are not authorized"));
    }

    @Test
    @DisplayName("Should handle validation errors and return 400 with field errors")
    void handleValidationErrors_Returns400() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("");

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }
}
