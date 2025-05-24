package com.chrono.event.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chrono.event.dto.EventDTO;
import com.chrono.event.entity.Civilization;
import com.chrono.event.entity.Event;
import com.chrono.event.repository.CivilizationRepository;
import com.chrono.event.repository.EventRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CivilizationRepository civilizationRepository;

    public Event createEvent(EventDTO dto) {
        Civilization civ = civilizationRepository.findById(dto.getCivilizationId())
                .orElseThrow(() -> new IllegalArgumentException("Civilization not found"));

        Event event = Event.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .date(dto.getDate())
                .civilization(civ)
                .type(dto.getType())
                .build();

        return eventRepository.save(event);
    }

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public List<Event> getByCivilization(Long civId) {
        return eventRepository.findByCivilizationId(civId);
    }
}
