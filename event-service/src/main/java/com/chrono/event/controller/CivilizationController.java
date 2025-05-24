package com.chrono.event.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.event.dto.CivilizationDTO;
import com.chrono.event.entity.Civilization;
import com.chrono.event.service.CivilizationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/civilizations")
@Slf4j
public class CivilizationController {

    @Autowired
    private CivilizationService civilizationService;

    @GetMapping
    public ResponseEntity<List<CivilizationDTO>> getAll() {
        log.info("Récupération de toutes les civilisations");
        List<CivilizationDTO> civilizations = civilizationService.getAllDTO();
        return ResponseEntity.ok(civilizations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CivilizationDTO> getById(@PathVariable Long id) {
        log.info("Récupération de la civilisation avec l'ID: {}", id);
        CivilizationDTO civilization = civilizationService.getByIdDTO(id);
        return ResponseEntity.ok(civilization);
    }

    @PostMapping
    public ResponseEntity<CivilizationDTO> create(@RequestBody Civilization civilization) {
        log.info("Création d'une nouvelle civilisation: {}", civilization.getName());
        CivilizationDTO created = civilizationService.createDTO(civilization);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CivilizationDTO> update(@PathVariable Long id, @RequestBody Civilization civilization) {
        log.info("Mise à jour de la civilisation avec l'ID: {}", id);
        CivilizationDTO updated = civilizationService.updateDTO(id, civilization);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Suppression de la civilisation avec l'ID: {}", id);
        civilizationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
