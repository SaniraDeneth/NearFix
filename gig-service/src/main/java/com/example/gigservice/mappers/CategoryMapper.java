package com.example.gigservice.mappers;

import com.example.gigservice.dtos.CategoryDto;
import com.example.gigservice.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    @Mapping(source = "parent.id", target = "parentId")
    CategoryDto toDto(Category category);
}
