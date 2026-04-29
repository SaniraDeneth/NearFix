package com.example.gigservice.mappers;

import com.example.gigservice.dtos.GigDto;
import com.example.gigservice.entities.Gig;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GigMapper {
    GigDto toDto(Gig gig);
}
