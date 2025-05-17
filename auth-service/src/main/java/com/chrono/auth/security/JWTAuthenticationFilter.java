package com.chrono.auth.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends GenericFilter {
    private final JWTService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final String authHeader = req.getHeader("Authorization");
        final String requestUri = req.getRequestURI();
        
        // Log pour debug
        log.debug("Request URI: {}, Auth header: {}", requestUri, authHeader != null ? "Present" : "Absent");
        
        // Skip le filtre pour les chemins publics
        if (requestUri.startsWith("/api/auth/") || requestUri.equals("/api/test/public")) {
            chain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);
            String email = jwtService.extractEmail(jwt);
            
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("User authenticated: {}", email);
            }
        } catch (Exception e) {
            log.error("Erreur lors de la validation du token JWT: {}", e.getMessage());
            // Ne pas propager l'exception, continuer la chaîne de filtres
            // pour que Spring Security gère l'erreur d'authentification
        }
        
        chain.doFilter(request, response);
    }
}
