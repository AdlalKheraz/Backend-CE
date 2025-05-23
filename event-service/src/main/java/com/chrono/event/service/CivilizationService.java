package com.chrono.event.service;

import java.util.List;

import com.chrono.event.entity.Civilization;

public interface CivilizationService {
    List<Civilization> getAll();
    Civilization getById(Long id);
    Civilization create(Civilization civilization);
    Civilization update(Long id, Civilization civilization);
    void delete(Long id);
}
