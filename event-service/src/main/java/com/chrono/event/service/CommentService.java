package com.chrono.event.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chrono.event.dto.CommentDTO;
import com.chrono.event.entity.Comment;
import com.chrono.event.entity.Event;
import com.chrono.event.exception.CommentNotFoundException;
import com.chrono.event.exception.EventNotFoundException;
import com.chrono.event.repository.CommentRepository;
import com.chrono.event.repository.EventRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private EventRepository eventRepository;

    public Comment createComment(CommentDTO dto) {
        log.info("Création d'un commentaire pour l'événement: {}", dto.getEventId());
        
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Événement non trouvé avec l'ID: " + dto.getEventId()));

        Comment comment = Comment.builder()
                .authorEmail(dto.getAuthorEmail())
                .content(dto.getContent())
                .event(event)
                .postedAt(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Commentaire créé avec succès avec l'ID: {}", savedComment.getId());
        
        return savedComment;
    }

    public List<Comment> getByEvent(Long eventId) {
        log.info("Récupération des commentaires pour l'événement: {}", eventId);
        
        // Vérifier que l'événement existe
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Événement non trouvé avec l'ID: " + eventId);
        }
        
        List<Comment> comments = commentRepository.findByEventId(eventId);
        
        if (comments.isEmpty()) {
            log.info("Aucun commentaire trouvé pour l'événement: {}", eventId);
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} commentaire(s) pour l'événement: {}", comments.size(), eventId);
        return comments;
    }

    public Comment getCommentById(Long id) {
        log.info("Récupération du commentaire avec l'ID: {}", id);
        
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Commentaire non trouvé avec l'ID: " + id));
    }

    public Comment updateComment(Long id, CommentDTO dto) {
        log.info("Mise à jour du commentaire avec l'ID: {}", id);
        
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Commentaire non trouvé avec l'ID: " + id));
        
        existingComment.setContent(dto.getContent());
        Comment updatedComment = commentRepository.save(existingComment);
        
        log.info("Commentaire mis à jour avec succès");
        return updatedComment;
    }

    public void deleteComment(Long id) {
        log.info("Suppression du commentaire avec l'ID: {}", id);
        
        if (!commentRepository.existsById(id)) {
            throw new CommentNotFoundException("Commentaire non trouvé avec l'ID: " + id);
        }
        
        commentRepository.deleteById(id);
        log.info("Commentaire supprimé avec succès");
    }

    public List<Comment> getAllComments() {
        log.info("Récupération de tous les commentaires");
        
        List<Comment> allComments = commentRepository.findAll();
        
        if (allComments.isEmpty()) {
            log.info("Aucun commentaire trouvé dans la base de données");
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} commentaire(s) au total", allComments.size());
        return allComments;
    }
    
    /**
     * Récupère tous les commentaires sous forme de DTOs
     */
    public List<CommentDTO> getAllCommentsAsDTO() {
        log.info("Récupération de tous les commentaires sous forme de DTOs");
        
        List<Comment> allComments = commentRepository.findAll();
        
        if (allComments.isEmpty()) {
            log.info("Aucun commentaire trouvé dans la base de données");
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} commentaire(s) au total", allComments.size());
        return allComments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convertit un commentaire en DTO
     */
    private CommentDTO convertToDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .authorEmail(comment.getAuthorEmail())
                .content(comment.getContent())
                .postedAt(comment.getPostedAt())
                .eventId(comment.getEvent().getId())
                .build();
    }
}
