package com.chrono.event.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chrono.event.dto.PublicEventDTO;
import com.chrono.event.entity.Event;
import com.chrono.event.exception.EventNotFoundException;
import com.chrono.event.repository.EventRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PublicEventService {

    @Autowired
    private EventRepository eventRepository;

    /**
     * Récupère tous les événements avec les informations publiques limitées
     */
    public List<PublicEventDTO> getAllPublicEvents() {
        log.info("Récupération de tous les événements publics");
        
        List<Event> events = eventRepository.findAll();
        
        if (events.isEmpty()) {
            log.info("Aucun événement trouvé dans la base de données");
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} événement(s) au total", events.size());
        return events.stream()
                .map(this::convertToPublicDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère un événement spécifique avec les informations publiques limitées
     */
    public PublicEventDTO getPublicEventById(Long id) {
        log.info("Récupération de l'événement public avec l'ID: {}", id);
        
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Événement non trouvé avec l'ID: " + id));
        
        return convertToPublicDTO(event);
    }
    
    /**
     * Récupère tous les événements d'une civilisation avec les informations publiques limitées
     */
    public List<PublicEventDTO> getPublicEventsByCivilization(Long civId) {
        log.info("Récupération des événements publics pour la civilisation: {}", civId);
        
        List<Event> events = eventRepository.findByCivilizationId(civId);
        
        if (events.isEmpty()) {
            log.info("Aucun événement trouvé pour la civilisation: {}", civId);
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} événement(s) pour la civilisation: {}", events.size(), civId);
        return events.stream()
                .map(this::convertToPublicDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convertit une entité Event en DTO public
     */
    private PublicEventDTO convertToPublicDTO(Event event) {
        return PublicEventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getDate())
                .civilizationId(event.getCivilization().getId())
                .civilizationName(event.getCivilization().getName())
                .type(event.getType())
                .verified(event.isVerified())
                .build();
    }
}
