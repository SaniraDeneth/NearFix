package com.example.gigservice.controllers;

import com.example.gigservice.dtos.CategoryDto;
import com.example.gigservice.dtos.CreateCategoryRequest;
import com.example.gigservice.dtos.UpdateCategoryRequest;
import com.example.gigservice.exceptions.ResourceNotFoundException;
import com.example.gigservice.services.CategoryService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @DisplayName("POST /categories - Should return 201 Created")
    void createCategory_Returns201() throws Exception {
        var categoryId = UUID.randomUUID();
        
        var request = new CreateCategoryRequest();
        request.setName("Home Services");

        var responseDto = new CategoryDto();
        responseDto.setId(categoryId);
        responseDto.setName("Home Services");

        when(categoryService.createCategory(any(CreateCategoryRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/categories/" + categoryId))
                .andExpect(jsonPath("$.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.name").value("Home Services"));
    }

    @Test
    @DisplayName("GET /categories/{id} - Should return 200 OK")
    void getCategoryById_Returns200() throws Exception {
        var categoryId = UUID.randomUUID();

        var responseDto = new CategoryDto();
        responseDto.setId(categoryId);
        responseDto.setName("Plumbing");

        when(categoryService.getCategoryById(categoryId)).thenReturn(responseDto);

        mockMvc.perform(get("/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.name").value("Plumbing"));
    }

    @Test
    @DisplayName("GET /categories/{id} - Should return 404 when not found")
    void getCategoryById_WhenNotFound_Returns404() throws Exception {
        var categoryId = UUID.randomUUID();

        when(categoryService.getCategoryById(categoryId))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(get("/categories/{id}", categoryId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Category not found"));
    }

    @Test
    @DisplayName("GET /categories - Should return 200 OK with list")
    void getAllCategories_Returns200() throws Exception {
        var responseDto = new CategoryDto();
        responseDto.setName("Home Services");

        when(categoryService.getAllCategories()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Home Services"));
    }

    @Test
    @DisplayName("PUT /categories/{id} - Should return 200 OK")
    void updateCategory_Returns200() throws Exception {
        var categoryId = UUID.randomUUID();

        var request = new UpdateCategoryRequest();
        request.setName("Updated Services");

        var responseDto = new CategoryDto();
        responseDto.setId(categoryId);
        responseDto.setName("Updated Services");

        when(categoryService.updateCategory(eq(categoryId), any(UpdateCategoryRequest.class))).thenReturn(responseDto);

        mockMvc.perform(put("/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Services"));
    }

    @Test
    @DisplayName("DELETE /categories/{id} - Should return 204 No Content")
    void deleteCategory_Returns204() throws Exception {
        var categoryId = UUID.randomUUID();

        doNothing().when(categoryService).deleteCategory(categoryId);

        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isNoContent());
    }
}
