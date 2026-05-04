package com.example.authservice.mappers;

import com.example.authservice.dto.UserDto;
import com.example.authservice.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);
}
