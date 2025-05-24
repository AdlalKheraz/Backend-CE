package com.chrono.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chrono.event.entity.Civilization;

@Repository
public interface CivilizationRepository extends JpaRepository<Civilization, Long> {
}
