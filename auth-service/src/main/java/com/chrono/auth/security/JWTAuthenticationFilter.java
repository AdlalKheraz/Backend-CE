package com.chrono.auth.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Autowired
    private JWTService jwtService;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {
        
        final String requestURI = request.getRequestURI();
        log.debug("Requête reçue: {}", requestURI);
        
        // Si c'est une route auth, pas besoin de vérifier le token
        if (requestURI.startsWith("/auth/")) {
            log.debug("Route auth, aucune vérification de token nécessaire");
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extraire le token s'il existe
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        log.debug("En-tête Authorization: {}", authHeader != null ? 
                (authHeader.startsWith(BEARER_PREFIX) ? BEARER_PREFIX + "..." : "Invalide") : "Absent");
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            if (!requestURI.startsWith("/auth/")) {
                // Pour les routes non-auth, l'absence de token est consignée mais
                // le filtre continue (la sécurité bloquera ensuite si nécessaire)
                log.warn("Tentative d'accès sans token JWT à: {}", requestURI);
            }
            filterChain.doFilter(request, response);
            return;
        }
        
        final String jwt = authHeader.substring(BEARER_PREFIX.length());
        
        try {
            final String email = jwtService.extractEmail(jwt);
            log.debug("Email extrait du token: {}", email);
            
            // Authentifier l'utilisateur s'il n'est pas déjà authentifié
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                log.debug("Utilisateur chargé: {}, rôles: {}", email, userDetails.getAuthorities());
                
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                log.debug("Utilisateur authentifié avec succès: {}", email);
            }
        } catch (Exception e) {
            log.error("Impossible de valider le token JWT: {}", e.getMessage());
            // Ne pas bloquer ici, la configuration de sécurité s'en chargera
        }
        
        filterChain.doFilter(request, response);
    }
}
