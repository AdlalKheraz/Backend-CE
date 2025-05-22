package com.chrono.media.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chrono.media.config.StorageConfig;
import com.chrono.media.service.StorageService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageConfig storageConfig) {
        this.rootLocation = Paths.get(storageConfig.getUploadDir());
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            log.info("Dossier de stockage initialisé : {}", rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier de stockage", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Impossible de stocker un fichier vide");
            }
            
            // Générer un nom de fichier unique pour éviter les conflits
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            
            // Copier le fichier vers le dossier de stockage
            Path destinationFile = this.rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();
            
            // Vérifier que le chemin est bien dans le dossier de stockage (sécurité)
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Impossible de stocker le fichier en dehors du dossier de stockage");
            }
            
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                log.info("Fichier stocké avec succès : {}", filename);
                
                // Retourner l'URL relative pour accéder au fichier
                return "/uploads/" + filename;
            }
        } catch (IOException e) {
            throw new RuntimeException("Échec du stockage du fichier", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public void delete(String filename) {
        try {
            if (filename == null || filename.isEmpty()) {
                return;
            }
            
            // Extraire le nom du fichier de l'URL
            String filenameOnly = filename;
            if (filename.startsWith("/uploads/")) {
                filenameOnly = filename.substring("/uploads/".length());
            }
            
            Path file = load(filenameOnly);
            Files.deleteIfExists(file);
            log.info("Fichier supprimé avec succès : {}", filenameOnly);
        } catch (IOException e) {
            log.error("Impossible de supprimer le fichier: {}", filename, e);
        }
    }
} 