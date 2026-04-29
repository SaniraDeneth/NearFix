package com.example.gigservice.mappers;

import com.example.gigservice.dtos.GigDto;
import com.example.gigservice.dtos.PointDto;
import com.example.gigservice.entities.Gig;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface GigMapper {
    
    @Mapping(source = "location", target = "location")
    GigDto toDto(Gig gig);

    default PointDto mapPoint(Point point) {
        if (point == null) {
            return null;
        }
        return new PointDto(point.getY(), point.getX());
    }
}
