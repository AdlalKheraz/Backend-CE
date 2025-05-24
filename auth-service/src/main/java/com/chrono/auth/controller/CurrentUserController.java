package com.chrono.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.auth.dto.UserResponse;
import com.chrono.auth.entity.User;
import com.chrono.auth.exception.UserNotFoundException;
import com.chrono.auth.repository.UserRepository;
import com.chrono.auth.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour les opérations sur le profil utilisateur courant
 */
@RestController
@RequestMapping("/users/me")
@Slf4j
@Validated
public class CurrentUserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Endpoint pour vérifier que le token utilisateur fonctionne 
     * et récupérer ses informations
     */
    @GetMapping
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();
        log.info("GET /users/me - Récupération des infos pour l'utilisateur: {}", currentUserEmail);
        
        try {
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'email: " + currentUserEmail));
            
            UserResponse userResponse = userService.toUserResponse(currentUser);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des informations pour l'utilisateur {}: {}", 
                    currentUserEmail, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Endpoint pour supprimer son propre compte
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteOwnAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();
        log.info("DELETE /users/me - Suppression du compte pour l'utilisateur: {}", currentUserEmail);
        
        try {
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'email: " + currentUserEmail));
            
            userService.deleteUser(currentUser.getId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du compte pour l'utilisateur {}: {}", 
                    currentUserEmail, e.getMessage(), e);
            throw e;
        }
    }
} 