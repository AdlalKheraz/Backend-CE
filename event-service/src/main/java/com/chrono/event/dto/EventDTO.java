package com.chrono.event.dto;

import java.time.LocalDate;

import com.chrono.event.entity.EventType;

import lombok.Data;

@Data
public class EventDTO {
    private String title;
    private String description;
    private LocalDate date;
    private Long civilizationId;
    private EventType type;
}
