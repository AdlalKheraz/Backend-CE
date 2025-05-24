package com.chrono.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {
    
    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Size(min = 1, max = 50, message = "Le prénom doit contenir entre 1 et 50 caractères")
    private String firstName;
    
    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 1, max = 50, message = "Le nom doit contenir entre 1 et 50 caractères")
    private String lastName;
    
    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "Format d'email invalide")
    private String email;
    
    // Champs optionnels pour la mise à jour du mot de passe
    private String currentPassword;
    
    @Size(min = 6, message = "Le nouveau mot de passe doit contenir au moins 6 caractères")
    private String newPassword;
    
    private String confirmPassword;
} 