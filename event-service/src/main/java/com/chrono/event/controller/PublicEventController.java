package com.chrono.event.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.event.dto.PublicEventDTO;
import com.chrono.event.service.EventService;
import com.chrono.event.service.PublicEventService;

@RestController
@RequestMapping("/events/public")
public class PublicEventController {

    @Autowired
    private PublicEventService eventService;

    @GetMapping
    public List<PublicEventDTO> getAllPublicEvents() {
        return eventService.getAllPublicEvents();
    }

    @GetMapping("/{id}")
    public PublicEventDTO getPublicEventById(@PathVariable Long id) {
        return eventService.getPublicEventById(id);
    }

    @GetMapping("/civilization/{id}")
    public List<PublicEventDTO> getPublicEventsByCivilization(@PathVariable Long id) {
        return eventService.getPublicEventsByCivilization(id);
    }
}
