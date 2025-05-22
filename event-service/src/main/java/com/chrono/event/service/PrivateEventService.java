package com.chrono.event.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chrono.event.dto.EventDTO;
import com.chrono.event.dto.PrivateEventDTO;
import com.chrono.event.entity.Civilization;
import com.chrono.event.entity.Event;
import com.chrono.event.repository.CivilizationRepository;
import com.chrono.event.repository.EventRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PrivateEventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CivilizationRepository civilizationRepository;

    /**
     * Crée un nouvel événement avec les informations détaillées
     */
    public PrivateEventDTO createEvent(PrivateEventDTO dto) {
        Civilization civ = civilizationRepository.findById(dto.getCivilizationId())
                .orElseThrow(() -> new IllegalArgumentException("Civilization not found"));

        Event event = Event.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .fullDescription(dto.getFullDescription()) // Initialisation avec la description standard
                .date(dto.getDate())
                .civilization(civ)
                .type(dto.getType())
                .verified(false) // Par défaut, un nouvel événement n'est pas vérifié
                .build();

        Event savedEvent = eventRepository.save(event);
        return convertToPrivateDTO(savedEvent);
    }

    /**
     * Met à jour un événement existant
     */
    public PrivateEventDTO updateEvent(Long id, PrivateEventDTO dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
        
        Civilization civ = civilizationRepository.findById(dto.getCivilizationId())
                .orElseThrow(() -> new IllegalArgumentException("Civilization not found"));
        
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setFullDescription(dto.getFullDescription());
        event.setDate(dto.getDate());
        event.setCivilization(civ);
        event.setType(dto.getType());
        
        Event updatedEvent = eventRepository.save(event);
        return convertToPrivateDTO(updatedEvent);
    }
    
    /**
     * Supprime un événement
     */
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }
    
    /**
     * Marque un événement comme vérifié
     */
    public PrivateEventDTO verifyEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
        
        event.setVerified(true);
        Event updatedEvent = eventRepository.save(event);
        return convertToPrivateDTO(updatedEvent);
    }
    
    /**
     * Récupère tous les événements avec les informations détaillées
     */
    public List<PrivateEventDTO> getAllPrivateEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToPrivateDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère un événement spécifique avec les informations détaillées
     */
    public PrivateEventDTO getPrivateEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
        return convertToPrivateDTO(event);
    }
    
    /**
     * Récupère tous les événements d'une civilisation avec les informations détaillées
     */
    public List<PrivateEventDTO> getPrivateEventsByCivilization(Long civId) {
        return eventRepository.findByCivilizationId(civId).stream()
                .map(this::convertToPrivateDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convertit une entité Event en DTO privé
     */
    private PrivateEventDTO convertToPrivateDTO(Event event) {
        PrivateEventDTO dto = new PrivateEventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setFullDescription(event.getFullDescription());
        dto.setDate(event.getDate());
        dto.setCivilizationId(event.getCivilization().getId());
        dto.setCivilizationName(event.getCivilization().getName());
        dto.setType(event.getType());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        dto.setSource(event.getSource());
        dto.setVerified(event.isVerified());
        
        // Ajout des informations utilisateur si disponibles
        if (event.getUserId() != null) {
            dto.setUserId(event.getUserId());
        }
        if (event.getUserRole() != null) {
            dto.setUserRole(event.getUserRole());
        }
        if (event.getUserEmail() != null) {
            dto.setUserEmail(event.getUserEmail());
        }
        
        return dto;
    }
}
