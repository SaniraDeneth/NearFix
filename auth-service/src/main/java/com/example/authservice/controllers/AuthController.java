package com.example.authservice.controllers;

import com.example.authservice.config.JwtConfig;
import com.example.authservice.dto.*;
import com.example.authservice.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtConfig jwtConfig;

    @Value("${services.internal-secret}")
    private String internalSecret;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> register(
            @Valid @RequestBody RegisterRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        var userDto = authService.register(request);
        var uri = uriBuilder.path("/users/" + userDto.getId()).build().toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        TokenDto tokens = authService.login(request);
        var cookie = new Cookie("refreshToken", tokens.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(tokens.getAccessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        TokenDto tokens = authService.refreshToken(refreshToken);
        
        var cookie = new Cookie("refreshToken", tokens.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(tokens.getAccessToken()));
    }

    @PutMapping("/users/{userId}/upgrade-role")
    public ResponseEntity<UserDto> upgradeRole(
            @PathVariable UUID userId,
            @RequestHeader(value = "X-Internal-Secret", required = false) String secretHeader
    ) {
        if (secretHeader == null || !secretHeader.equals(internalSecret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var userDto = authService.upgradeUserToProvider(userId);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }
        
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        return ResponseEntity.noContent().build();
    }
}
