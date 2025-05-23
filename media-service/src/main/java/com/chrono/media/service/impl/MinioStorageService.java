package com.chrono.media.service.impl;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chrono.media.exception.StorageException;
import com.chrono.media.service.StorageService;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "minio")
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioStorageService(MinioClient minioClient, @Value("${minio.bucket-name}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @PostConstruct
    public void init() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            );
            
            if (!bucketExists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
                );
                log.info("Bucket créé: {}", bucketName);
            } else {
                log.info("Bucket existant: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation du stockage MinIO", e);
            throw new StorageException("Erreur lors de l'initialisation du stockage", e);
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
            
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(filename)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
                );
            }
            
            String fileUrl = "/api/media/files/" + filename;
            log.info("Fichier stocké avec succès: {}", filename);
            return fileUrl;
            
        } catch (Exception e) {
            log.error("Erreur lors du stockage du fichier", e);
            throw new StorageException("Échec du stockage du fichier", e);
        }
    }

    @Override
    public Path load(String filename) {
        return Paths.get(filename);
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
            
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filenameOnly)
                    .build()
            );
            
            log.info("Fichier supprimé avec succès: {}", filenameOnly);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du fichier: {}", filename, e);
        }
    }
    
    public String getPresignedUrl(String filename) {
        try {
            String filenameOnly = filename;
            if (filename.startsWith("/api/media/files/")) {
                filenameOnly = filename.substring("/api/media/files/".length());
            }
            
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(filenameOnly)
                    .expiry(1, TimeUnit.HOURS)
                    .build()
            );
        } catch (Exception e) {
            log.error("Erreur lors de la génération de l'URL présignée", e);
            throw new StorageException("Échec de la génération de l'URL présignée", e);
        }
    }
} 