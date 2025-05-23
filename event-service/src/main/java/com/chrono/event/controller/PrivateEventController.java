package com.chrono.event.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.event.dto.EventFilterDTO;
import com.chrono.event.dto.PrivateEventDTO;
import com.chrono.event.service.PrivateEventService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/events")
@Slf4j
public class PrivateEventController {

    @Autowired
    private PrivateEventService eventService;

    @PostMapping
    public PrivateEventDTO create(@RequestBody PrivateEventDTO dto) {
        log.info("Création d'un nouvel événement");
        return eventService.createEvent(dto);
    }

    /**
     * Récupère tous les événements avec pagination et filtrage
     */
    @PostMapping("/search")
    public Page<PrivateEventDTO> searchEvents(@RequestBody EventFilterDTO filter) {
        log.info("Recherche d'événements avec filtres: {}", filter);
        return eventService.getAllPrivateEvents(filter);
    }

    /**
     * Méthode pour récupérer tous les événements sans filtrage
     */
    @GetMapping
    public List<PrivateEventDTO> getAllPrivateEvents() {
        log.info("Récupération de tous les événements");
        return eventService.getAllPrivateEvents();
    }

    @GetMapping("/{id}")
    public PrivateEventDTO getPrivateEventById(@PathVariable Long id) {
        log.info("Récupération de l'événement avec l'ID: {}", id);
        return eventService.getPrivateEventById(id);
    }

    /**
     * Récupère les événements d'une civilisation avec pagination et filtrage
     */
    @PostMapping("/civilization/{id}/search")
    public Page<PrivateEventDTO> searchEventsByCivilization(
            @PathVariable Long id, 
            @RequestBody EventFilterDTO filter) {
        log.info("Recherche d'événements pour la civilisation {} avec filtres: {}", id, filter);
        return eventService.getPrivateEventsByCivilization(id, filter);
    }

    /**
     * Méthode pour récupérer les événements d'une civilisation sans filtrage
     */
    @GetMapping("/civilization/{id}")
    public List<PrivateEventDTO> getPrivateEventsByCivilization(@PathVariable Long id) {
        log.info("Récupération des événements pour la civilisation: {}", id);
        return eventService.getPrivateEventsByCivilization(id);
    }
    
    @PutMapping("/{id}")
    public PrivateEventDTO updateEvent(@PathVariable Long id, @RequestBody PrivateEventDTO dto) {
        log.info("Mise à jour de l'événement avec l'ID: {}", id);
        return eventService.updateEvent(id, dto);
    }
    
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        log.info("Suppression de l'événement avec l'ID: {}", id);
        eventService.deleteEvent(id);
    }
    
    @PutMapping("/{id}/verify")
    public PrivateEventDTO verifyEvent(@PathVariable Long id) {
        log.info("Vérification de l'événement avec l'ID: {}", id);
        return eventService.verifyEvent(id);
    }
}
