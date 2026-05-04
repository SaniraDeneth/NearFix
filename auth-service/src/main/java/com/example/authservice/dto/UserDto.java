package com.example.authservice.dto;

import com.example.authservice.entities.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String email;
    private Role role;
}