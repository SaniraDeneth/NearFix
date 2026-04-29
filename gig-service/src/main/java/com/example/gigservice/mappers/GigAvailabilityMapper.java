package com.example.gigservice.mappers;

import com.example.gigservice.dtos.GigAvailabilityDto;
import com.example.gigservice.entities.GigAvailability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GigAvailabilityMapper {
    
    @Mapping(source = "gig.id", target = "gigId")
    GigAvailabilityDto toDto(GigAvailability availability);
}
