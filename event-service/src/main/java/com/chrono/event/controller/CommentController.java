package com.chrono.event.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.event.dto.CommentDTO;
import com.chrono.event.entity.Comment;
import com.chrono.event.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public Comment create(@RequestBody CommentDTO dto) {
        return commentService.createComment(dto);
    }

    @GetMapping("/event/{eventId}")
    public List<Comment> getByEvent(@PathVariable Long eventId) {
        return commentService.getByEvent(eventId);
    }
}
