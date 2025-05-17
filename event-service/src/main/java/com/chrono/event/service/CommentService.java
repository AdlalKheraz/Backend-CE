package com.chrono.event.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chrono.event.dto.CommentDTO;
import com.chrono.event.entity.Comment;
import com.chrono.event.entity.Event;
import com.chrono.event.repository.CommentRepository;
import com.chrono.event.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    public Comment createComment(CommentDTO dto) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Comment comment = Comment.builder()
                .authorEmail(dto.getAuthorEmail())
                .content(dto.getContent())
                .event(event)
                .postedAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    public List<Comment> getByEvent(Long eventId) {
        return commentRepository.findByEventId(eventId);
    }
}
