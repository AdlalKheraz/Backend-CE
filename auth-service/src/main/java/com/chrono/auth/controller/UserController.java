package com.chrono.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.auth.dto.UpdateRoleRequest;
import com.chrono.auth.dto.UpdateUserRequest;
import com.chrono.auth.entity.User;
import com.chrono.auth.repository.UserRepository;
import com.chrono.auth.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("GET /users - Utilisateur: {}, Rôles: {}", auth.getName(), auth.getAuthorities());
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("GET /users/{} - Utilisateur: {}, Rôles: {}", id, auth.getName(), auth.getAuthorities());
        
        try {
            // Vérifier si l'utilisateur est admin ou s'il accède à son propre profil
            if (hasAdminRole(auth) || isUserAccessingOwnProfile(auth, id)) {
                User user = userService.getUserById(id);
                return ResponseEntity.ok(user);
            } else {
                log.warn("Accès non autorisé - User {} tente d'accéder au profil {}", auth.getName(), id);
                throw new RuntimeException("Accès non autorisé à ce profil utilisateur");
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'utilisateur {}: {}", id, e.getMessage());
            throw e;
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("PUT /users/{} - Utilisateur: {}, Rôles: {}", id, auth.getName(), auth.getAuthorities());
        
        // Vérifier si l'utilisateur est admin ou s'il accède à son propre profil
        if (hasAdminRole(auth) || isUserAccessingOwnProfile(auth, id)) {
            User updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok(updatedUser);
        } else {
            log.warn("Accès non autorisé - User {} tente de modifier le profil {}", auth.getName(), id);
            throw new RuntimeException("Accès non autorisé à ce profil utilisateur");
        }
    }
    
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("PUT /users/{}/role - Utilisateur: {}, Rôles: {}", id, auth.getName(), auth.getAuthorities());
        
        User updatedUser = userService.updateUserRole(id, request);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Vérifie si l'utilisateur a le rôle ADMIN
     */
    private boolean hasAdminRole(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        log.debug("Vérification rôle ADMIN: {}", isAdmin);
        return isAdmin;
    }
    
    /**
     * Vérifie si l'utilisateur accède à son propre profil
     */
    private boolean isUserAccessingOwnProfile(Authentication auth, Long userId) {
        String currentUserEmail = auth.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        boolean isOwnProfile = currentUser.getId().equals(userId);
        log.debug("Vérification profil utilisateur - ID demandé: {}, ID utilisateur: {}, Accès au propre profil: {}", 
                userId, currentUser.getId(), isOwnProfile);
        return isOwnProfile;
    }
    
    /**
     * Méthode existante pour la rétrocompatibilité, utilise les nouvelles méthodes helpers
     */
    private void checkUserAuthorization(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Vérification d'autorisation - User ID demandé: {}, Utilisateur authentifié: {}", userId, auth.getName());
        
        if (!hasAdminRole(auth) && !isUserAccessingOwnProfile(auth, userId)) {
            log.warn("Accès non autorisé - User {} tente d'accéder au profil {}", auth.getName(), userId);
            throw new RuntimeException("Accès non autorisé à ce profil utilisateur");
        }
        
        log.debug("Accès autorisé");
    }
} 