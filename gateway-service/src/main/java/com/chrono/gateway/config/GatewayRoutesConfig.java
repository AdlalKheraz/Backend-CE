package com.chrono.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chrono.gateway.filter.JwtAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class GatewayRoutesConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayRoutesConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("Configuration des routes de la gateway");
        
        return builder.routes()
            .route("auth-service", r -> r.path("/api/auth/**")
                .filters(f -> f.stripPrefix(1)
                              .rewritePath("/auth/(?<segment>.*)", "/auth/${segment}"))
                .uri("http://localhost:8081"))
            .route("users-service", r -> r.path("/api/users/**")
                .filters(f -> {
                    log.debug("Configuration du filtre pour /api/users/**");
                    return f.stripPrefix(1)
                            .rewritePath("/users/(?<segment>.*)", "/users/${segment}")
                            .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()));
                })
                .uri("http://localhost:8081"))
            .route("media-service", r -> r.path("/api/media/**")
                .filters(f -> f.stripPrefix(2)
                              .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                .uri("http://localhost:8082"))
            // Route publique pour les événements publics (sans authentification JWT)
            .route("event-service-public", r -> r.path("/api/events/public/**")
                .filters(f -> f.rewritePath("/api/(?<segment>.*)", "/${segment}"))
                .uri("http://localhost:8083"))
            // Route protégée pour les autres endpoints du service d'événements
            .route("event-service", r -> r.path("/api/events/**", "/api/civilizations/**", "/api/comments/**")
                .filters(f -> f.rewritePath("/api/(?<segment>.*)", "/${segment}")
                              .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                .uri("http://localhost:8083"))
            .build();
    }
}
