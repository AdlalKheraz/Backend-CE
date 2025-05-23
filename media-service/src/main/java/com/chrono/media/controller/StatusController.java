package com.chrono.media.controller;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
@Slf4j
public class StatusController {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkStatus() {
        log.info("Vérification du statut du service media");
        
        Map<String, Object> status = new HashMap<>();
        status.put("service", "media-service");
        status.put("status", "UP");
        status.put("timestamp", System.currentTimeMillis());
        
        // Vérifier la connexion à la base de données
        try {
            boolean dbStatus = checkDatabaseConnection();
            status.put("database", dbStatus ? "Connected" : "Disconnected");
            
            if (dbStatus) {
                // Récupérer le nombre d'entrées dans la table media
                Integer mediaCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM media", Integer.class);
                status.put("mediaCount", mediaCount);
            } else {
                status.put("error", "Database connection failed");
            }
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de la base de données: {}", e.getMessage(), e);
            status.put("database", "Error");
            status.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(status);
    }
    
    private boolean checkDatabaseConnection() {
        try {
            // Vérifier si la connexion est valide
            return dataSource.getConnection().isValid(5); // timeout de 5 secondes
        } catch (Exception e) {
            log.error("Erreur de connexion à la base de données: {}", e.getMessage(), e);
            return false;
        }
    }
} 