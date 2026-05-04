package com.example.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 6, max = 12)
    private String password;
}
