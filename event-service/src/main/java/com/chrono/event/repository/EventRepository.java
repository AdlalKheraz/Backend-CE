package com.chrono.event.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chrono.event.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCivilizationId(Long civilizationId);
}
