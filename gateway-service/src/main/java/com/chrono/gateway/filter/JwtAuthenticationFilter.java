package com.chrono.gateway.filter;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    public JwtAuthenticationFilter() {
        super(Config.class);
    }
    
    @Data
    public static class Config {
        // Configuration vide
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Skip pour les routes d'auth
            if (isAuthRoute(request)) {
                return chain.filter(exchange);
            }

            // Récupérer le token du header Authorization
            List<String> authHeader = request.getHeaders().get("Authorization");
            if (authHeader == null || authHeader.isEmpty() || !authHeader.get(0).startsWith("Bearer ")) {
                log.error("Header d'autorisation manquant ou invalide");
                return onError(exchange, "Header d'autorisation manquant ou invalide", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.get(0).substring(7);
            try {
                // Valider le token
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
                
                // Ajouter les infos utilisateur aux headers pour les services en aval
                ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Email", claims.getSubject())
                    .header("X-User-Role", claims.get("role", String.class))
                    .build();
                
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                log.error("Token JWT invalide: {}", e.getMessage());
                return onError(exchange, "Token JWT invalide", HttpStatus.UNAUTHORIZED);
            }
        };
    }
    
    private boolean isAuthRoute(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return path.startsWith("/api/auth/");
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
} 