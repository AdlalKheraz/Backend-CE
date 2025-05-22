package com.chrono.event.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chrono.event.dto.PublicEventDTO;
import com.chrono.event.entity.Event;
import com.chrono.event.repository.EventRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PublicEventService {

    @Autowired
    private EventRepository eventRepository;

    /**
     * Récupère tous les événements avec les informations publiques limitées
     */
    public List<PublicEventDTO> getAllPublicEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToPublicDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère un événement spécifique avec les informations publiques limitées
     */
    public PublicEventDTO getPublicEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
        return convertToPublicDTO(event);
    }
    
    /**
     * Récupère tous les événements d'une civilisation avec les informations publiques limitées
     */
    public List<PublicEventDTO> getPublicEventsByCivilization(Long civId) {
        return eventRepository.findByCivilizationId(civId).stream()
                .map(this::convertToPublicDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convertit une entité Event en DTO public
     */
    private PublicEventDTO convertToPublicDTO(Event event) {
        PublicEventDTO dto = new PublicEventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setCivilizationName(event.getCivilization().getName());
        dto.setType(event.getType());
        return dto;
    }
}
