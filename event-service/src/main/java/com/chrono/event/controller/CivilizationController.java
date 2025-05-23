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
    public ResponseEntity<List<Civilization>> getAll() {
        log.info("Récupération de toutes les civilisations");
        List<Civilization> civilizations = civilizationService.getAll();
        return ResponseEntity.ok(civilizations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Civilization> getById(@PathVariable Long id) {
        log.info("Récupération de la civilisation avec l'ID: {}", id);
        Civilization civilization = civilizationService.getById(id);
        return ResponseEntity.ok(civilization);
    }

    @PostMapping
    public ResponseEntity<Civilization> create(@RequestBody Civilization civilization) {
        log.info("Création d'une nouvelle civilisation: {}", civilization.getName());
        Civilization created = civilizationService.create(civilization);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Civilization> update(@PathVariable Long id, @RequestBody Civilization civilization) {
        log.info("Mise à jour de la civilisation avec l'ID: {}", id);
        Civilization updated = civilizationService.update(id, civilization);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Suppression de la civilisation avec l'ID: {}", id);
        civilizationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
