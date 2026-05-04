package com.example.authservice.services;

import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.dto.UserDto;
import com.example.authservice.exceptions.EmailAlreadyExistsException;
import com.example.authservice.exceptions.EmailNotFoundException;
import com.example.authservice.exceptions.InvalidCredentialsException;
import com.example.authservice.mappers.UserMapper;
import com.example.authservice.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use: " + request.getEmail());
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto login(@Valid LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EmailNotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        return userMapper.toDto(user);
    }
}
