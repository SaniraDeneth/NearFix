package com.example.authservice.config;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
@Data
public class JwtConfig {
    private String secret;
    private int accessTokenExpiration;
    private int refreshTokenExpiration;

    public SecretKey getSecretKey() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT Secret must be at least 32 characters long for HS256");
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
