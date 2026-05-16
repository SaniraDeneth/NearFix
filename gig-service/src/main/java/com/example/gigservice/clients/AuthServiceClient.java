package com.example.gigservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@FeignClient(name = "auth-service", url = "${services.auth-service.url}")
public interface AuthServiceClient {

    @PutMapping("/auth/users/{userId}/upgrade-role")
    void upgradeUserToProvider(@PathVariable("userId") UUID userId);
}
