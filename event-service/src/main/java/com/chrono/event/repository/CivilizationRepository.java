package com.chrono.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chrono.event.entity.Civilization;

public interface CivilizationRepository extends JpaRepository<Civilization, Long> {
}
