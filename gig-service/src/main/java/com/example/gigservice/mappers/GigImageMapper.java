package com.example.gigservice.mappers;

import com.example.gigservice.dtos.GigImageDto;
import com.example.gigservice.entities.GigImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GigImageMapper {
    
    @Mapping(source = "gig.id", target = "gigId")
    GigImageDto toDto(GigImage gigImage);
}
