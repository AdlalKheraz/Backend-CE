package com.chrono.event.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.event.dto.CommentDTO;
import com.chrono.event.service.CommentService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/comments")
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDTO> create(@RequestBody CommentDTO dto) {
        log.info("Création d'un nouveau commentaire pour l'événement: {}", dto.getEventId());
        CommentDTO comment = commentService.createCommentAndReturnDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments() {
        log.info("Récupération de tous les commentaires");
        List<CommentDTO> comments = commentService.getAllCommentsAsDTO();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        log.info("Récupération du commentaire avec l'ID: {}", id);
        CommentDTO comment = commentService.getCommentByIdAsDTO(id);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<CommentDTO>> getByEvent(@PathVariable Long eventId) {
        log.info("Récupération des commentaires pour l'événement: {}", eventId);
        List<CommentDTO> comments = commentService.getByEventAsDTO(eventId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long id, @RequestBody CommentDTO dto) {
        log.info("Mise à jour du commentaire avec l'ID: {}", id);
        CommentDTO updatedComment = commentService.updateCommentAndReturnDTO(id, dto);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.info("Suppression du commentaire avec l'ID: {}", id);
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
