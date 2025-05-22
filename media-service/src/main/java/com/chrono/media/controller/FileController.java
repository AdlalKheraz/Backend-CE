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
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String filename) {
        try {
            // Récupérer l'objet de MinIO
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(filename)
                    .build();

            InputStream stream = minioClient.getObject(args);
            
            // Déterminer le type de média
            String contentType = "application/octet-stream";
            if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (filename.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (filename.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            } else if (filename.toLowerCase().endsWith(".mp4")) {
                contentType = "video/mp4";
            } else if (filename.toLowerCase().endsWith(".webm")) {
                contentType = "video/webm";
            } else if (filename.toLowerCase().endsWith(".avi")) {
                contentType = "video/x-msvideo";
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            
            // Configurer le cache pour les navigateurs
            headers.setCacheControl("max-age=31536000");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(stream));
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du fichier: {}", filename, e);
            return ResponseEntity.notFound().build();
        }
    }
} 