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

@RestController
@RequestMapping("/events")
public class PrivateEventController {

    @Autowired
    private PrivateEventService eventService;

    @PostMapping
    public PrivateEventDTO create(@RequestBody PrivateEventDTO dto) {
        return eventService.createEvent(dto);
    }

    /**
     * Récupère tous les événements avec pagination et filtrage
     */
    @PostMapping("/search")
    public Page<PrivateEventDTO> searchEvents(@RequestBody EventFilterDTO filter) {
        return eventService.getAllPrivateEvents(filter);
    }

    /**
     * Méthode de compatibilité pour récupérer tous les événements sans filtrage
     */
    @GetMapping
    public List<PrivateEventDTO> getAllPrivateEvents() {
        return eventService.getAllPrivateEvents();
    }

    @GetMapping("/{id}")
    public PrivateEventDTO getPrivateEventById(@PathVariable Long id) {
        return eventService.getPrivateEventById(id);
    }

    /**
     * Récupère les événements d'une civilisation avec pagination et filtrage
     */
    @PostMapping("/civilization/{id}/search")
    public Page<PrivateEventDTO> searchEventsByCivilization(
            @PathVariable Long id, 
            @RequestBody EventFilterDTO filter) {
        return eventService.getPrivateEventsByCivilization(id, filter);
    }

    /**
     * Méthode de compatibilité pour récupérer les événements d'une civilisation sans filtrage
     */
    @GetMapping("/civilization/{id}")
    public List<PrivateEventDTO> getPrivateEventsByCivilization(@PathVariable Long id) {
        return eventService.getPrivateEventsByCivilization(id);
    }
    
    @PutMapping("/{id}")
    public PrivateEventDTO updateEvent(@PathVariable Long id, @RequestBody PrivateEventDTO dto) {
        return eventService.updateEvent(id, dto);
    }
    
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
    
    @PutMapping("/{id}/verify")
    public PrivateEventDTO verifyEvent(@PathVariable Long id) {
        return eventService.verifyEvent(id);
    }
}
