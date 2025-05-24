package com.chrono.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chrono.auth.dto.AuthRequest;
import com.chrono.auth.dto.AuthResponse;
import com.chrono.auth.dto.RegisterRequest;
import com.chrono.auth.entity.Role;
import com.chrono.auth.entity.User;
import com.chrono.auth.exception.EmailAlreadyExistsException;
import com.chrono.auth.exception.UserNotFoundException;
import com.chrono.auth.repository.UserRepository;
import com.chrono.auth.security.JWTService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public AuthResponse register(RegisterRequest request) {
        log.info("Tentative d'inscription pour l'email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Tentative d'inscription avec un email déjà existant: {}", request.getEmail());
            throw new EmailAlreadyExistsException("Cet email est déjà utilisé");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Utilisateur créé avec succès: {}", savedUser.getEmail());

        String token = jwtService.generateToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole().name())
                .expiresIn(jwtExpiration)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        log.info("Tentative d'authentification pour l'email: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

            String token = jwtService.generateToken(user);
            log.info("Authentification réussie pour l'utilisateur: {}", user.getEmail());

            return AuthResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole().name())
                    .expiresIn(jwtExpiration)
                    .build();

        } catch (Exception e) {
            log.error("Échec de l'authentification pour l'email: {}", request.getEmail(), e);
            throw e;
        }
    }
}
