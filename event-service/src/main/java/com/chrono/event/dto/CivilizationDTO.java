package com.chrono.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CivilizationDTO {
    private Long id;
    private String name;
    private String description;
    private String period;
} 