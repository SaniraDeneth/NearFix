package com.example.authservice.services;

import com.example.authservice.config.JwtConfig;
import com.example.authservice.dto.*;
import com.example.authservice.entities.RefreshToken;
import com.example.authservice.entities.Role;
import com.example.authservice.entities.User;
import com.example.authservice.exceptions.EmailAlreadyExistsException;
import com.example.authservice.exceptions.EmailNotFoundException;
import com.example.authservice.exceptions.InvalidTokenException;
import com.example.authservice.mappers.UserMapper;
import com.example.authservice.repositories.RefreshTokenRepository;
import com.example.authservice.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use: " + request.getEmail());
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        
        if (user.getRole() == null) {
            user.setRole(Role.CLIENT);
        }

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Transactional
    public TokenDto login(@Valid LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EmailNotFoundException("User not found after authentication"));

        var accessToken = jwtService.generateAccessToken(user).toString();
        var refreshToken = jwtService.generateRefreshToken(user).toString();

        saveRefreshToken(user, refreshToken);

        return new TokenDto(accessToken, refreshToken);
    }

    @Transactional
    public JwtResponse refreshToken(String refreshTokenString) {
        var jwt = jwtService.parseToken(refreshTokenString);
        if (jwt == null || jwt.isExpired()) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found in database"));

        var user = refreshToken.getUser();
        String newAccessToken = jwtService.generateAccessToken(user).toString();
        
        return new JwtResponse(newAccessToken);
    }

    @Transactional
    public void logout(String refreshTokenString) {
        refreshTokenRepository.deleteByToken(refreshTokenString);
    }

    private void saveRefreshToken(User user, String token) {
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);

        refreshToken.setExpiryDate(Instant.now().plusSeconds(jwtConfig.getRefreshTokenExpiration()));
        
        refreshTokenRepository.save(refreshToken);
    }
}
