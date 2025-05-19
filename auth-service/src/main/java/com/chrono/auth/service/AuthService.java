package com.chrono.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chrono.auth.dto.AuthRequest;
import com.chrono.auth.dto.AuthResponse;
import com.chrono.auth.dto.RegisterRequest;
import com.chrono.auth.entity.Role;
import com.chrono.auth.entity.User;
import com.chrono.auth.repository.UserRepository;
import com.chrono.auth.security.JWTService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JWTService jwtService;
    
    @Autowired
    private AuthenticationManager authManager;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public AuthResponse register(RegisterRequest request) {
        log.info("Enregistrement d'un nouvel utilisateur: {}", request.getEmail());
        
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }
        
        // Créer le nouvel utilisateur
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        
        // Sauvegarder l'utilisateur
        userRepository.save(user);
        
        // Générer le token
        String token = jwtService.generateToken(user);
        
        // Créer et retourner la réponse
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresIn(jwtExpiration)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        log.info("Tentative d'authentification: {}", request.getEmail());
        
        // Authentifier l'utilisateur
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        // Récupérer l'utilisateur
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Générer le token
        String token = jwtService.generateToken(user);
        
        // Créer et retourner la réponse
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresIn(jwtExpiration)
                .build();
    }
}
