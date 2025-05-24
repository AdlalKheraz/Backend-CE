package com.chrono.media.controller;

import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.media.config.MinioConfig;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/media/files")
@RequiredArgsConstructor
public class FileController {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String filename) {
        log.info("Demande d'accès au fichier: {}", filename);
        
        try {
            // Récupérer l'objet de MinIO
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(filename)
                    .build();

            InputStream stream = minioClient.getObject(args);
            
            // Déterminer le type de média
            String contentType = determineContentType(filename);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            
            // Configurer le cache pour les navigateurs
            headers.setCacheControl("max-age=31536000");
            
            log.info("Fichier {} récupéré avec succès, type: {}", filename, contentType);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(stream));
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du fichier: {}", filename, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Détermine le type MIME en fonction de l'extension du fichier
     */
    private String determineContentType(String filename) {
        if (filename == null) {
            return "application/octet-stream";
        }
        
        String lowerFilename = filename.toLowerCase();
        
        if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFilename.endsWith(".png")) {
            return "image/png";
        } else if (lowerFilename.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFilename.endsWith(".mp4")) {
            return "video/mp4";
        } else if (lowerFilename.endsWith(".webm")) {
            return "video/webm";
        } else if (lowerFilename.endsWith(".avi")) {
            return "video/x-msvideo";
        }
        
        return "application/octet-stream";
    }
} 