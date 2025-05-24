package com.chrono.event.dto;

import java.time.LocalDate;
import java.util.List;

import com.chrono.event.entity.EventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicEventEnrichedDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    
    // Juste l'ID de la civilisation
    private Long civilizationId;
    
    private EventType type;
    private boolean verified;
    
    // Médias associés
    private List<MediaDTO> medias;
} 