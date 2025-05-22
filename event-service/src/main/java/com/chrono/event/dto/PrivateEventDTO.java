package com.chrono.event.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.chrono.event.entity.EventType;

import lombok.Data;

@Data
public class PrivateEventDTO {
    private Long id;
    private String title;
    private String description;
    private String fullDescription; // Description complète pour les utilisateurs connectés
    private LocalDate date;
    private Long civilizationId;
    private String civilizationName;
    private EventType type;
    private LocalDateTime createdAt; // Date de création de l'événement
    private LocalDateTime updatedAt; // Date de dernière mise à jour
    private String source; // Source de l'information
    private boolean verified; // Si l'événement a été vérifié
    
    // Informations sur l'utilisateur
    private String userId;
    private String userRole;
    private String userEmail;
}
