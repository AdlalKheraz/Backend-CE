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
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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
            String path = request.getPath().toString();
            
            log.debug("JWT Filter - Traitement de la requête: {}", path);
            
            // Skip pour les routes d'auth et les routes publiques
            if (isPublicRoute(request)) {
                log.debug("Route publique, aucune vérification de token nécessaire");
                return chain.filter(exchange);
            }

            // Récupérer le token du header Authorization
            List<String> authHeader = request.getHeaders().get("Authorization");
            if (authHeader == null || authHeader.isEmpty()) {
                log.warn("Header d'autorisation manquant pour: {}", path);
                return onError(exchange, "Header d'autorisation manquant", HttpStatus.UNAUTHORIZED);
            }
            
            String authHeaderValue = authHeader.get(0);
            if (!authHeaderValue.startsWith("Bearer ")) {
                log.warn("Format du header d'autorisation invalide: {}", authHeaderValue);
                return onError(exchange, "Format du header d'autorisation invalide", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeaderValue.substring(7);
            log.debug("Token trouvé pour: {}", path);
            
            try {
                // Valider le token
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
                
                String userEmail = claims.getSubject();
                String userRole = claims.get("role", String.class);
                log.debug("Token valide pour l'utilisateur: {}, rôle: {}", userEmail, userRole);
                
                // Ajouter les infos utilisateur aux headers pour les services en aval
                ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Email", userEmail)
                    .header("X-User-Role", userRole)
                    .build();
                
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (ExpiredJwtException e) {
                log.error("Token JWT expiré: {}", e.getMessage());
                return onError(exchange, "Token JWT expiré", HttpStatus.UNAUTHORIZED);
            } catch (UnsupportedJwtException e) {
                log.error("Token JWT non supporté: {}", e.getMessage());
                return onError(exchange, "Token JWT non supporté", HttpStatus.UNAUTHORIZED);
            } catch (MalformedJwtException e) {
                log.error("Token JWT mal formé: {}", e.getMessage());
                return onError(exchange, "Token JWT mal formé", HttpStatus.UNAUTHORIZED);
            } catch (SignatureException e) {
                log.error("Signature du token JWT invalide: {}", e.getMessage());
                return onError(exchange, "Signature du token JWT invalide", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                log.error("Erreur lors de la validation du token JWT: {}", e.getMessage());
                return onError(exchange, "Token JWT invalide", HttpStatus.UNAUTHORIZED);
            }
        };
    }
    
    private boolean isPublicRoute(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        
        // Routes d'authentification
        boolean isAuthPath = path.startsWith("/api/auth/");
        
        // Routes GET publiques (Comments et Civilizations)
        boolean isPublicGetPath = "GET".equals(method) && 
                                  (path.startsWith("/api/comments") || 
                                   path.startsWith("/api/civilizations"));
        
        boolean isPublic = isAuthPath || isPublicGetPath;
        log.debug("Vérification si route publique: {} ({}) -> {}", path, method, isPublic);
        return isPublic;
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        log.debug("Réponse d'erreur envoyée: {} - {}", status, message);
        return response.setComplete();
    }
} 