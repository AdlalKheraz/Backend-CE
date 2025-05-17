package com.chrono.event.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private String authorEmail;
    private String content;
    private Long eventId;
}
