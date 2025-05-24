package com.chrono.event.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.chrono.event.dto.EventFilterDTO;
import com.chrono.event.dto.PrivateEventDTO;
import com.chrono.event.entity.Civilization;
import com.chrono.event.entity.Event;
import com.chrono.event.repository.CivilizationRepository;
import com.chrono.event.repository.EventRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;

@Service
public class PrivateEventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CivilizationRepository civilizationRepository;

    @Autowired
    private MediaClientService mediaClientService;

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
     * Supprime un événement et tous les médias associés
     */
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event not found with id: " + id);
        }
        
        // Supprimer les médias associés à l'événement
        try {
            mediaClientService.deleteMediaByEventId(id);
        } catch (Exception e) {
            // Log l'erreur mais continue la suppression de l'événement
            System.err.println("Erreur lors de la suppression des médias de l'événement " + id + ": " + e.getMessage());
        }
        
        // Supprimer l'événement (les commentaires seront supprimés automatiquement grâce à CascadeType.REMOVE)
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
     * Récupère tous les événements avec les informations détaillées
     * avec filtrage et pagination
     */
    public Page<PrivateEventDTO> getAllPrivateEvents(EventFilterDTO filter) {
        // Création des critères de pagination et de tri
        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDirection()), filter.getSortBy());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        
        // Création des spécifications pour le filtrage
        Specification<Event> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            
            // Filtre par titre
            if (StringUtils.hasText(filter.getTitle())) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), 
                        "%" + filter.getTitle().toLowerCase() + "%"));
            }
            
            // Filtre par description
            if (StringUtils.hasText(filter.getDescription())) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), 
                        "%" + filter.getDescription().toLowerCase() + "%"));
            }
            
            // Filtre par date (de)
            if (filter.getDateFrom() != null) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.greaterThanOrEqualTo(root.get("date"), filter.getDateFrom()));
            }
            
            // Filtre par date (à)
            if (filter.getDateTo() != null) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.lessThanOrEqualTo(root.get("date"), filter.getDateTo()));
            }
            
            // Filtre par civilisation
            if (filter.getCivilizationId() != null) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.equal(root.get("civilization").get("id"), filter.getCivilizationId()));
            }
            
            // Filtre par type
            if (filter.getType() != null) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.equal(root.get("type"), filter.getType()));
            }
            
            // Filtre par vérification
            if (filter.getVerified() != null) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.equal(root.get("verified"), filter.getVerified()));
            }
            
            return predicate;
        };
        
        // Exécution de la requête avec les spécifications et la pagination
        Page<Event> events = eventRepository.findAll(spec, pageable);
        
        // Conversion des résultats en DTOs
        return events.map(this::convertToPrivateDTO);
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
     * Récupère un événement spécifique avec les informations détaillées
     * avec application des filtres si spécifiés
     */
    public PrivateEventDTO getPrivateEventById(Long id, EventFilterDTO filter) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
        
        // Si des filtres sont spécifiés, vérifier que l'événement correspond aux critères
        if (filter != null) {
            boolean matchesFilter = true;
            
            // Vérification du titre
            if (matchesFilter && StringUtils.hasText(filter.getTitle())) {
                matchesFilter = event.getTitle().toLowerCase().contains(filter.getTitle().toLowerCase());
            }
            
            // Vérification de la description
            if (matchesFilter && StringUtils.hasText(filter.getDescription())) {
                matchesFilter = event.getDescription().toLowerCase().contains(filter.getDescription().toLowerCase());
            }
            
            // Vérification de la date (de)
            if (matchesFilter && filter.getDateFrom() != null) {
                matchesFilter = !event.getDate().isBefore(filter.getDateFrom());
            }
            
            // Vérification de la date (à)
            if (matchesFilter && filter.getDateTo() != null) {
                matchesFilter = !event.getDate().isAfter(filter.getDateTo());
            }
            
            // Vérification de la civilisation
            if (matchesFilter && filter.getCivilizationId() != null) {
                matchesFilter = event.getCivilization().getId().equals(filter.getCivilizationId());
            }
            
            // Vérification du type
            if (matchesFilter && filter.getType() != null) {
                matchesFilter = event.getType() == filter.getType();
            }
            
            // Vérification du statut de vérification
            if (matchesFilter && filter.getVerified() != null) {
                matchesFilter = event.isVerified() == filter.getVerified();
            }
            
            // Si l'événement ne correspond pas aux critères, lancer une exception
            if (!matchesFilter) {
                throw new EntityNotFoundException("Event with id " + id + " does not match the specified criteria");
            }
        }
        
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
     * Récupère tous les événements d'une civilisation avec les informations détaillées
     * avec filtrage et pagination
     */
    public Page<PrivateEventDTO> getPrivateEventsByCivilization(Long civId, EventFilterDTO filter) {
        // Vérification que la civilisation existe
        if (!civilizationRepository.existsById(civId)) {
            throw new EntityNotFoundException("Civilization not found with id: " + civId);
        }
        
        // Création des critères de pagination et de tri
        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDirection()), filter.getSortBy());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        
        // Création des spécifications pour le filtrage
        Specification<Event> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("civilization").get("id"), civId);
            
            // Filtre par titre
            if (StringUtils.hasText(filter.getTitle())) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), 
                        "%" + filter.getTitle().toLowerCase() + "%"));
            }
            
            // Filtre par description
            if (StringUtils.hasText(filter.getDescription())) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), 
                        "%" + filter.getDescription().toLowerCase() + "%"));
            }
            
            // Filtre par date (de)
            if (filter.getDateFrom() != null) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.greaterThanOrEqualTo(root.get("date"), filter.getDateFrom()));
            }
            
            // Filtre par date (à)
            if (filter.getDateTo() != null) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.lessThanOrEqualTo(root.get("date"), filter.getDateTo()));
            }
            
            // Filtre par type
            if (filter.getType() != null) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.equal(root.get("type"), filter.getType()));
            }
            
            // Filtre par vérification
            if (filter.getVerified() != null) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.equal(root.get("verified"), filter.getVerified()));
            }
            
            return predicate;
        };
        
        // Exécution de la requête avec les spécifications et la pagination
        Page<Event> events = eventRepository.findAll(spec, pageable);
        
        // Conversion des résultats en DTOs
        return events.map(this::convertToPrivateDTO);
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

    /**
     * Méthode utilitaire pour définir la valeur d'un champ par réflexion
     */
    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}
