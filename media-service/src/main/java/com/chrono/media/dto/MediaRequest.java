package com.chrono.media.dto;

import com.chrono.media.entity.MediaType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaRequest {
    @NotBlank
    private String url;
    
    private String title;
    
    private String description;
    
    @NotNull
    private MediaType type;
    
    @NotNull
    private Long eventId;
}
