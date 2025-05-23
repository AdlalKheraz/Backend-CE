package com.chrono.media.dto;

import com.chrono.media.entity.MediaType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaResponse {
    private Long id;
    private String url;
    private String title;
    private String description;
    private MediaType type;
    private Long eventId;
}
