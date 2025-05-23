package com.chrono.event.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chrono.event.entity.Civilization;
import com.chrono.event.exception.CivilizationNotFoundException;
import com.chrono.event.repository.CivilizationRepository;
import com.chrono.event.service.CivilizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CivilizationServiceImpl implements CivilizationService {

    private final CivilizationRepository civilizationRepository;

    @Override
    public List<Civilization> getAll() {
        log.info("Récupération de toutes les civilisations");
        
        List<Civilization> civilizations = civilizationRepository.findAll();
        
        if (civilizations.isEmpty()) {
            log.info("Aucune civilisation trouvée dans la base de données");
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} civilisation(s) au total", civilizations.size());
        return civilizations;
    }

    @Override
    public Civilization getById(Long id) {
        log.info("Récupération de la civilisation avec l'ID: {}", id);
        
        return civilizationRepository.findById(id)
                .orElseThrow(() -> new CivilizationNotFoundException("Civilisation non trouvée avec l'ID: " + id));
    }

    @Override
    public Civilization create(Civilization civilization) {
        log.info("Création d'une nouvelle civilisation: {}", civilization.getName());
        Civilization savedCivilization = civilizationRepository.save(civilization);
        log.info("Civilisation créée avec succès avec l'ID: {}", savedCivilization.getId());
        return savedCivilization;
    }

    @Override
    public Civilization update(Long id, Civilization civilization) {
        log.info("Mise à jour de la civilisation avec l'ID: {}", id);
        
        Civilization existingCivilization = civilizationRepository.findById(id)
                .orElseThrow(() -> new CivilizationNotFoundException("Civilisation non trouvée avec l'ID: " + id));
        
        existingCivilization.setName(civilization.getName());
        existingCivilization.setDescription(civilization.getDescription());
        existingCivilization.setStartDate(civilization.getStartDate());
        existingCivilization.setEndDate(civilization.getEndDate());
        
        Civilization updatedCivilization = civilizationRepository.save(existingCivilization);
        log.info("Civilisation mise à jour avec succès: {}", updatedCivilization.getName());
        return updatedCivilization;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression de la civilisation avec l'ID: {}", id);
        
        if (!civilizationRepository.existsById(id)) {
            throw new CivilizationNotFoundException("Civilisation non trouvée avec l'ID: " + id);
        }
        
        civilizationRepository.deleteById(id);
        log.info("Civilisation supprimée avec succès");
    }
}
