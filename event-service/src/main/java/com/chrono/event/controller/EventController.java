package com.chrono.event.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.event.dto.EventDTO;
import com.chrono.event.entity.Event;
import com.chrono.event.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public Event create(@RequestBody EventDTO dto) {
        return eventService.createEvent(dto);
    }

    @GetMapping
    public List<Event> getAll() {
        return eventService.getAll();
    }

    @GetMapping("/civilization/{id}")
    public List<Event> getByCivilization(@PathVariable Long id) {
        return eventService.getByCivilization(id);
    }
}
