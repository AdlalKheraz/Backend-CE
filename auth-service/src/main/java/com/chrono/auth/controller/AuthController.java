package com.chrono.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.auth.dto.AuthRequest;
import com.chrono.auth.dto.AuthResponse;
import com.chrono.auth.dto.RegisterRequest;
import com.chrono.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        
        // Créer un cookie sécurisé avec le token JWT
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", response.getToken())
            .httpOnly(true)          // Empêche l'accès via JavaScript
            .secure(true)            // Envoyé uniquement en HTTPS
            .path("/")               // Disponible pour tout le site
            .maxAge(24 * 60 * 60)    // Expire après 24h (en secondes)
            .sameSite("Strict")      // Protection CSRF
            .build();
            
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        
        // Créer un cookie sécurisé avec le token JWT
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", response.getToken())
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(24 * 60 * 60)
            .sameSite("Strict")
            .build();
            
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .body(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Créer un cookie vide avec une durée de vie de 0 seconde pour l'effacer
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)  // Expire immédiatement
            .sameSite("Strict")
            .build();
            
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .body(Map.of("message", "Déconnexion réussie"));
    }
}
