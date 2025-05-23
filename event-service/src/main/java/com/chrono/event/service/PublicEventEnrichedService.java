package com.chrono.event.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chrono.event.dto.CivilizationDTO;
import com.chrono.event.dto.CommentDTO;
import com.chrono.event.dto.PublicEventEnrichedDTO;
import com.chrono.event.entity.Civilization;
import com.chrono.event.entity.Comment;
import com.chrono.event.entity.Event;
import com.chrono.event.exception.EventNotFoundException;
import com.chrono.event.repository.CommentRepository;
import com.chrono.event.repository.EventRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PublicEventEnrichedService {

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private MediaClientService mediaClientService;

    /**
     * Récupère tous les événements avec les informations enrichies
     */
    public List<PublicEventEnrichedDTO> getAllEnrichedPublicEvents() {
        log.info("Récupération de tous les événements publics enrichis");
        
        List<Event> events = eventRepository.findAll();
        
        if (events.isEmpty()) {
            log.info("Aucun événement trouvé dans la base de données");
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} événement(s) au total", events.size());
        return events.stream()
                .map(this::convertToEnrichedDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère un événement spécifique avec les informations enrichies
     */
    public PublicEventEnrichedDTO getEnrichedPublicEventById(Long id) {
        log.info("Récupération de l'événement public enrichi avec l'ID: {}", id);
        
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Événement non trouvé avec l'ID: " + id));
        
        return convertToEnrichedDTO(event);
    }
    
    /**
     * Récupère tous les événements d'une civilisation avec les informations enrichies
     */
    public List<PublicEventEnrichedDTO> getEnrichedPublicEventsByCivilization(Long civId) {
        log.info("Récupération des événements publics enrichis pour la civilisation: {}", civId);
        
        List<Event> events = eventRepository.findByCivilizationId(civId);
        
        if (events.isEmpty()) {
            log.info("Aucun événement trouvé pour la civilisation: {}", civId);
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} événement(s) pour la civilisation: {}", events.size(), civId);
        return events.stream()
                .map(this::convertToEnrichedDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convertit une entité Event en DTO enrichi
     */
    private PublicEventEnrichedDTO convertToEnrichedDTO(Event event) {
        // Récupération des commentaires
        List<Comment> comments = commentRepository.findByEventId(event.getId());
        List<CommentDTO> commentDTOs = comments.stream()
                .map(this::convertToCommentDTO)
                .collect(Collectors.toList());
        
        // Récupération des médias
        var mediaDTOs = mediaClientService.getMediaByEventId(event.getId());
        
        // Conversion de la civilisation
        Civilization civilization = event.getCivilization();
        String period = "";
        if (civilization.getStartDate() != null) {
            period = civilization.getStartDate().toString();
            if (civilization.getEndDate() != null) {
                period += " - " + civilization.getEndDate().toString();
            }
        }
        
        CivilizationDTO civilizationDTO = CivilizationDTO.builder()
                .id(civilization.getId())
                .name(civilization.getName())
                .description(civilization.getDescription())
                .period(period)
                .build();
        
        return PublicEventEnrichedDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getDate())
                .civilization(civilizationDTO)
                .type(event.getType())
                .verified(event.isVerified())
                .comments(commentDTOs)
                .medias(mediaDTOs)
                .build();
    }
    
    /**
     * Convertit une entité Comment en DTO
     */
    private CommentDTO convertToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .authorEmail(comment.getAuthorEmail())
                .content(comment.getContent())
                .postedAt(comment.getPostedAt())
                .eventId(comment.getEvent().getId())
                .build();
    }
} 