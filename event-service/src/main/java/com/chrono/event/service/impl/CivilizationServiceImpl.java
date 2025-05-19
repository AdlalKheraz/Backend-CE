package com.chrono.event.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chrono.event.entity.Civilization;
import com.chrono.event.repository.CivilizationRepository;
import com.chrono.event.service.CivilizationService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
public class CivilizationServiceImpl implements CivilizationService {

    @Autowired
    private CivilizationRepository repository;

    @Override
    public List<Civilization> getAll() {
        return repository.findAll();
    }

    @Override
    public Civilization create(Civilization civilization) {
        return repository.save(civilization);
    }

    @Override
    public Civilization update(Long id, Civilization updated) {
        Civilization existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Civilization not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
