package com.chrono.media.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chrono.media.config.StorageConfig;
import com.chrono.media.exception.StorageException;
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
            log.error("Impossible de créer le dossier de stockage", e);
            throw new StorageException("Impossible de créer le dossier de stockage", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new StorageException("Impossible de stocker un fichier vide");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "unknown";
            }
            
            String filename = System.currentTimeMillis() + "_" + originalFilename;
            Path destinationFile = this.rootLocation.resolve(filename).normalize().toAbsolutePath();
            
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Impossible de stocker le fichier en dehors du dossier de stockage");
            }
            
            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            log.info("Fichier stocké avec succès: {}", filename);
            return "/api/media/files/" + filename;
            
        } catch (IOException e) {
            log.error("Échec du stockage du fichier", e);
            throw new StorageException("Échec du stockage du fichier", e);
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
            
            String filenameOnly = filename;
            if (filename.startsWith("/api/media/files/")) {
                filenameOnly = filename.substring("/api/media/files/".length());
            }
            
            Path file = load(filenameOnly);
            Files.deleteIfExists(file);
            log.info("Fichier supprimé avec succès: {}", filenameOnly);
        } catch (IOException e) {
            log.error("Erreur lors de la suppression du fichier: {}", filename, e);
        }
    }
} 