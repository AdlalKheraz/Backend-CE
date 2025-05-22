package com.chrono.event.dto;

import java.time.LocalDate;

import com.chrono.event.entity.EventType;

import lombok.Data;

@Data
public class PublicEventDTO {
    private Long id;
    private String title;
    private String description; // Description courte pour les utilisateurs non connectés
    private LocalDate date;
    private String civilizationName; // Juste le nom de la civilisation au lieu de l'ID
    private EventType type;
}
