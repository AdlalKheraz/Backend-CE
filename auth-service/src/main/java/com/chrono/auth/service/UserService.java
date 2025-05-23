package com.chrono.auth.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chrono.auth.dto.ChangeRoleRequest;
import com.chrono.auth.dto.UserResponse;
import com.chrono.auth.entity.Role;
import com.chrono.auth.entity.User;
import com.chrono.auth.exception.EmailAlreadyExistsException;
import com.chrono.auth.exception.UserNotFoundException;
import com.chrono.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        log.info("Récupération de tous les utilisateurs");
        
        List<User> users = userRepository.findAll();
        
        if (users.isEmpty()) {
            log.info("Aucun utilisateur trouvé dans la base de données");
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} utilisateur(s) au total", users.size());
        return users.stream()
                .map(this::toUserResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        log.info("Récupération de l'utilisateur avec l'ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
        return toUserResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        log.info("Récupération de l'utilisateur avec l'email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'email: " + email));
        return toUserResponse(user);
    }

    public boolean existsByEmail(String email) {
        log.debug("Vérification de l'existence de l'email: {}", email);
        return userRepository.existsByEmail(email);
    }

    public UserResponse updateUser(Long id, User updatedUser) {
        log.info("Mise à jour de l'utilisateur avec l'ID: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        if (!existingUser.getEmail().equals(updatedUser.getEmail()) && 
            userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new EmailAlreadyExistsException("Cet email est déjà utilisé");
        }

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());

        User savedUser = userRepository.save(existingUser);
        log.info("Utilisateur mis à jour avec succès: {}", savedUser.getEmail());
        
        return toUserResponse(savedUser);
    }

    public UserResponse changeUserRole(Long id, ChangeRoleRequest request) {
        log.info("Changement de rôle pour l'utilisateur avec l'ID: {} vers {}", id, request.getRole());
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        try {
            Role newRole = Role.valueOf(request.getRole().toUpperCase());
            user.setRole(newRole);
            User savedUser = userRepository.save(user);
            log.info("Rôle changé avec succès pour l'utilisateur: {}", savedUser.getEmail());
            return toUserResponse(savedUser);
        } catch (IllegalArgumentException e) {
            log.error("Rôle invalide: {}", request.getRole());
            throw new IllegalArgumentException("Rôle invalide: " + request.getRole());
        }
    }

    public void deleteUser(Long id) {
        log.info("Suppression de l'utilisateur avec l'ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("Utilisateur supprimé avec succès");
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
} 