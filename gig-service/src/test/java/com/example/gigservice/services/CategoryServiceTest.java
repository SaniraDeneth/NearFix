package com.example.gigservice.services;

import com.example.gigservice.dtos.CategoryDto;
import com.example.gigservice.dtos.CreateCategoryRequest;
import com.example.gigservice.dtos.UpdateCategoryRequest;
import com.example.gigservice.entities.Category;
import com.example.gigservice.exceptions.ResourceNotFoundException;
import com.example.gigservice.mappers.CategoryMapper;
import com.example.gigservice.repositories.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Should create a top-level category successfully")
    void createCategory_NoParent_Success() {
        var request = new CreateCategoryRequest();
        request.setName("Home Services");
        request.setParentId(null);

        var savedCategory = new Category();
        savedCategory.setName("Home Services");

        var expectedDto = new CategoryDto();
        expectedDto.setName("Home Services");

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(expectedDto);

        var result = categoryService.createCategory(request);

        assertNotNull(result);
        assertEquals("Home Services", result.getName());

        verify(categoryRepository, never()).findById(any());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Should create a sub-category successfully when parent exists")
    void createCategory_WithParent_Success() {
        var parentId = UUID.randomUUID();
        var request = new CreateCategoryRequest();
        request.setName("Plumbing");
        request.setParentId(parentId);

        var parentCategory = new Category();
        parentCategory.setId(parentId);
        parentCategory.setName("Home Services");

        var savedCategory = new Category();
        savedCategory.setName("Plumbing");
        savedCategory.setParent(parentCategory);

        var expectedDto = new CategoryDto();
        expectedDto.setName("Plumbing");

        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(expectedDto);

        var result = categoryService.createCategory(request);

        assertNotNull(result);
        assertEquals("Plumbing", result.getName());

        verify(categoryRepository).findById(parentId);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception when parent category not found during creation")
    void createCategory_ParentNotFound() {
        var parentId = UUID.randomUUID();
        var request = new CreateCategoryRequest();
        request.setName("Plumbing");
        request.setParentId(parentId);

        when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.createCategory(request));
        assertEquals("Parent category not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should return category when ID exists")
    void getCategoryById_Success() {
        var id = UUID.randomUUID();
        var category = new Category();
        category.setId(id);
        category.setName("Home Services");

        var expectedDto = new CategoryDto();
        expectedDto.setName("Home Services");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        var result = categoryService.getCategoryById(id);

        assertNotNull(result);
        assertEquals("Home Services", result.getName());
        verify(categoryRepository).findById(id);
    }

    @Test
    @DisplayName("Should throw exception when category ID not found")
    void getCategoryById_NotFound() {
        var id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(id));
        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should return list of all categories")
    void getAllCategories_Success() {
        var category1 = new Category();
        var category2 = new Category();
        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(new CategoryDto());

        var result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
        verify(categoryMapper, times(2)).toDto(any(Category.class));
    }

    @Test
    @DisplayName("Should update category successfully")
    void updateCategory_Success() {
        var id = UUID.randomUUID();
        var parentId = UUID.randomUUID();
        var request = new UpdateCategoryRequest();
        request.setName("Electrical Services");
        request.setParentId(parentId);

        var category = new Category();
        category.setName("Home Services");

        var parentCategory = new Category();
        parentCategory.setId(parentId);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(new CategoryDto());

        categoryService.updateCategory(id, request);

        assertEquals("Electrical Services", category.getName());
        assertEquals(parentCategory, category.getParent());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Should throw exception when category to update not found")
    void updateCategory_NotFound() {
        var id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(id, new UpdateCategoryRequest()));
    }

    @Test
    @DisplayName("Should throw exception when parent category not found during update")
    void updateCategory_ParentNotFound() {
        var id = UUID.randomUUID();
        var parentId = UUID.randomUUID();
        var request = new UpdateCategoryRequest();
        request.setParentId(parentId);

        var category = new Category();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(id, request));
        assertEquals("Parent category not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should update only category name successfully")
    void updateCategory_OnlyName_Success() {
        var id = UUID.randomUUID();
        var request = new UpdateCategoryRequest();
        request.setName("New Name");

        var category = new Category();
        category.setName("Old Name");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(new CategoryDto());

        categoryService.updateCategory(id, request);

        assertEquals("New Name", category.getName());
        verify(categoryRepository, never()).findById(request.getParentId());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Should update only parent category successfully")
    void updateCategory_OnlyParent_Success() {
        var id = UUID.randomUUID();
        var parentId = UUID.randomUUID();
        var request = new UpdateCategoryRequest();
        request.setParentId(parentId);

        var category = new Category();
        category.setName("Existing Name");

        var parentCategory = new Category();
        parentCategory.setId(parentId);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(new CategoryDto());

        categoryService.updateCategory(id, request);

        assertEquals("Existing Name", category.getName()); 
        assertEquals(parentCategory, category.getParent());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Should delete category successfully")
    void deleteCategory_Success() {
        var id = UUID.randomUUID();
        var category = new Category();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(id);

        verify(categoryRepository).delete(category);
    }

    @Test
    @DisplayName("Should throw exception when category to delete not found")
    void deleteCategory_NotFound() {
        var id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(id));
    }


}
