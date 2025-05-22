package com.chrono.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chrono.auth.dto.UpdateRoleRequest;
import com.chrono.auth.dto.UpdateUserRequest;
import com.chrono.auth.entity.Role;
import com.chrono.auth.entity.User;
import com.chrono.auth.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        log.info("Récupération de tous les utilisateurs");
        return userRepository.findAll();
    }
    
    public User getUserById(Long id) {
        log.info("Récupération de l'utilisateur avec l'ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
    }
    
    public User updateUser(Long id, UpdateUserRequest request) {
        log.info("Mise à jour de l'utilisateur avec l'ID: {}", id);
        
        User user = getUserById(id);
        
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Vérifier si l'email est déjà utilisé par un autre utilisateur
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw new RuntimeException("Cet email est déjà utilisé");
                        }
                    });
            user.setEmail(request.getEmail());
        }
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        
        return userRepository.save(user);
    }
    
    public User updateUserRole(Long id, UpdateRoleRequest request) {
        log.info("Mise à jour du rôle de l'utilisateur avec l'ID: {}", id);
        
        User user = getUserById(id);
        
        try {
            Role newRole = Role.valueOf(request.getRole().toUpperCase());
            user.setRole(newRole);
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rôle invalide: " + request.getRole());
        }
    }
} 