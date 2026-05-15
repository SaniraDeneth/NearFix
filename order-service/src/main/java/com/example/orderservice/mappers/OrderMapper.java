package com.example.orderservice.mappers;

import com.example.orderservice.dtos.OrderDto;
import com.example.orderservice.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
