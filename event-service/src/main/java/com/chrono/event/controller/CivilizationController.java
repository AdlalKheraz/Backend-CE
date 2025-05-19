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

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/civilizations")
public class CivilizationController {

    @Autowired
    private CivilizationService civilizationService;

    @GetMapping
    public List<Civilization> getAll() {
        return civilizationService.getAll();
    }

    @PostMapping
    public ResponseEntity<Civilization> create(@RequestBody Civilization civilization) {
        Civilization created = civilizationService.create(civilization);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Civilization> update(@PathVariable Long id, @RequestBody Civilization civilization) {
        Civilization updated = civilizationService.update(id, civilization);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        civilizationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
