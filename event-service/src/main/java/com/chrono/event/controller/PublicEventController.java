package com.chrono.event.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.event.dto.PublicEventDTO;
import com.chrono.event.service.PublicEventService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/events/public")
@Slf4j
public class PublicEventController {

    @Autowired
    private PublicEventService eventService;

    @GetMapping
    public List<PublicEventDTO> getAllPublicEvents() {
        log.info("Récupération de tous les événements publics");
        return eventService.getAllPublicEvents();
    }

    @GetMapping("/{id}")
    public PublicEventDTO getPublicEventById(@PathVariable Long id) {
        log.info("Récupération de l'événement public avec l'ID: {}", id);
        return eventService.getPublicEventById(id);
    }

    @GetMapping("/civilization/{id}")
    public List<PublicEventDTO> getPublicEventsByCivilization(@PathVariable Long id) {
        log.info("Récupération des événements publics pour la civilisation: {}", id);
        return eventService.getPublicEventsByCivilization(id);
    }
}
