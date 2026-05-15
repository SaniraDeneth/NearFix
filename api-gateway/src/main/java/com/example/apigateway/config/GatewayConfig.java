package com.example.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Value("${services.auth-service.url}")
    private String authServiceUrl;
    @Value("${services.gig-service.url}")
    private String gigServiceUrl;
    @Value("${services.order-service.url}")
    private String orderServiceUrl;

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-user-Id");
            if (userId != null) {
                return Mono.just(userId);
            }
            // Fallback to IP address if user is not authenticated
            return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
        };
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(2, 5);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, RedisRateLimiter rateLimiter) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .filters(f -> f.rewritePath("/api/(?<segment>.*)", "/${segment}")
                                .requestRateLimiter(rl -> rl.setRateLimiter(rateLimiter).setKeyResolver(userKeyResolver())))
                        .uri(authServiceUrl))
                .route("gig-service", r -> r.path("/api/gigs/**", "/api/categories/**")
                        .filters(f -> f.rewritePath("/api/(?<segment>.*)", "/${segment}")
                                .requestRateLimiter(rl -> rl.setRateLimiter(rateLimiter).setKeyResolver(userKeyResolver())))
                        .uri(gigServiceUrl))
                .route("order-service", r -> r.path("/api/orders/**")
                        .filters(f -> f.rewritePath("/api/(?<segment>.*)", "/${segment}")
                                .requestRateLimiter(rl -> rl.setRateLimiter(rateLimiter).setKeyResolver(userKeyResolver())))
                        .uri(orderServiceUrl))
                .build();
    }
}
