package com.chrono.event.dto;

import java.time.LocalDate;

import com.chrono.event.entity.EventType;

import lombok.Data;

@Data
public class EventFilterDTO {
    // Critères de filtrage
    private String title;
    private String description;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Long civilizationId;
    private EventType type;
    private Boolean verified;
    
    // Critères de pagination
    private Integer page = 0;
    private Integer size = 10;
    
    // Critères de tri
    private String sortBy = "date";
    private String sortDirection = "DESC";
}
