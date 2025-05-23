package com.chrono.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.auth.dto.ChangeRoleRequest;
import com.chrono.auth.dto.UserResponse;
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
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("GET /users - Utilisateur: {}, Rôles: {}", auth.getName(), auth.getAuthorities());
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("GET /users/{} - Utilisateur: {}, Rôles: {}", id, auth.getName(), auth.getAuthorities());
        
        try {
            // Vérifier si l'utilisateur est admin ou s'il accède à son propre profil
            if (hasAdminRole(auth) || isUserAccessingOwnProfile(auth, id)) {
                UserResponse user = userService.getUserById(id);
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
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody User userUpdate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("PUT /users/{} - Utilisateur: {}, Rôles: {}", id, auth.getName(), auth.getAuthorities());
        
        // Vérifier si l'utilisateur est admin ou s'il accède à son propre profil
        if (hasAdminRole(auth) || isUserAccessingOwnProfile(auth, id)) {
            UserResponse updatedUser = userService.updateUser(id, userUpdate);
            return ResponseEntity.ok(updatedUser);
        } else {
            log.warn("Accès non autorisé - User {} tente de modifier le profil {}", auth.getName(), id);
            throw new RuntimeException("Accès non autorisé à ce profil utilisateur");
        }
    }
    
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> changeUserRole(@PathVariable Long id, @RequestBody ChangeRoleRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("PATCH /users/{}/role - Utilisateur: {}, Rôles: {}", id, auth.getName(), auth.getAuthorities());
        
        UserResponse updatedUser = userService.changeUserRole(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("DELETE /users/{} - Utilisateur: {}, Rôles: {}", id, auth.getName(), auth.getAuthorities());
        
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
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
} 