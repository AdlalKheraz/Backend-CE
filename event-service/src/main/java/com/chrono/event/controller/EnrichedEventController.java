package com.chrono.event.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.event.dto.PublicEventEnrichedDTO;
import com.chrono.event.service.PublicEventEnrichedService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/events/enriched")
@Slf4j
public class EnrichedEventController {

    @Autowired
    private PublicEventEnrichedService enrichedEventService;

    @GetMapping
    public List<PublicEventEnrichedDTO> getAllEnrichedEvents() {
        log.info("Récupération de tous les événements enrichis via /events/enriched");
        return enrichedEventService.getAllEnrichedPublicEvents();
    }

    @GetMapping("/{id}")
    public PublicEventEnrichedDTO getEnrichedEventById(@PathVariable Long id) {
        log.info("Récupération de l'événement enrichi avec l'ID: {}", id);
        return enrichedEventService.getEnrichedPublicEventById(id);
    }

    @GetMapping("/civilization/{id}")
    public List<PublicEventEnrichedDTO> getEnrichedEventsByCivilization(@PathVariable Long id) {
        log.info("Récupération des événements enrichis pour la civilisation: {}", id);
        return enrichedEventService.getEnrichedPublicEventsByCivilization(id);
    }
} 