package com.chrono.media.service;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    
    /**
     * Stocke un fichier et retourne son URL relative
     */
    String store(MultipartFile file);
    
    /**
     * Supprime un fichier
     */
    void delete(String filename);
    
    /**
     * Charge un fichier comme ressource
     */
    Path load(String filename);
    
    /**
     * Initialise le stockage
     */
    void init();
} 