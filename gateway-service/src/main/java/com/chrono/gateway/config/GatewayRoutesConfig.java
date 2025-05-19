package com.chrono.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chrono.gateway.filter.JwtAuthenticationFilter;

@Configuration
public class GatewayRoutesConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayRoutesConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("auth-service", r -> r.path("/api/auth/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://localhost:8081"))
            .route("media-service", r -> r.path("/api/media/**")
                .filters(f -> f.stripPrefix(2)
                               .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                .uri("http://localhost:8082"))
            .route("event-service", r -> r.path("/api/events/**", "/api/civilizations/**", "/api/comments/**")
                .filters(f -> f.rewritePath("/api/(?<segment>.*)", "/${segment}")
                               .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                .uri("http://localhost:8083"))
            .build();
    }
}
