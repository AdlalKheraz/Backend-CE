package com.chrono.media.service.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chrono.media.config.MinioConfig;
import com.chrono.media.service.StorageService;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Primary
@Slf4j
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    @Autowired
    public MinioStorageService(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.bucketName = minioConfig.getBucketName();
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            // Vérifier si le bucket existe, sinon le créer
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket créé: {}", bucketName);
                
                // Configurer la politique de bucket pour le rendre public en lecture
                String policy = 
                    "{\n" +
                    "    \"Version\": \"2012-10-17\",\n" +
                    "    \"Statement\": [\n" +
                    "        {\n" +
                    "            \"Effect\": \"Allow\",\n" +
                    "            \"Principal\": {\"AWS\": [\"*\"]},\n" +
                    "            \"Action\": [\"s3:GetObject\"],\n" +
                    "            \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
                
                minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build()
                );
                log.info("Politique du bucket configurée pour permettre l'accès public en lecture");
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation du bucket MinIO", e);
            throw new RuntimeException("Erreur lors de l'initialisation du stockage", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Impossible de stocker un fichier vide");
        }
        
        try {
            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            
            // Définir le type de contenu
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            // Télécharger le fichier vers MinIO
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(contentType)
                    .build()
            );
            
            log.info("Fichier téléchargé avec succès: {}", filename);
            
            // Retourner l'URL du fichier
            return "/api/media/files/" + filename;
        } catch (Exception e) {
            log.error("Erreur lors du téléchargement du fichier", e);
            throw new RuntimeException("Échec du stockage du fichier", e);
        }
    }

    @Override
    public Path load(String filename) {
        // Cette méthode n'est pas réellement utilisée avec MinIO, mais nous gardons l'interface cohérente
        return Paths.get(filename);
    }

    @Override
    public void delete(String filename) {
        try {
            if (filename == null || filename.isEmpty()) {
                return;
            }
            
            // Extraire le nom du fichier de l'URL
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
    
    /**
     * Génère une URL présignée pour accéder temporairement au fichier
     */
    public String generatePresignedUrl(String filename, int expiryTime) {
        try {
            // Extraire le nom du fichier de l'URL
            String filenameOnly = filename;
            if (filename.startsWith("/api/media/files/")) {
                filenameOnly = filename.substring("/api/media/files/".length());
            }
            
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(filenameOnly)
                    .method(Method.GET)
                    .expiry(expiryTime, TimeUnit.MINUTES)
                    .build()
            );
        } catch (Exception e) {
            log.error("Erreur lors de la génération de l'URL présignée: {}", filename, e);
            throw new RuntimeException("Échec de la génération de l'URL présignée", e);
        }
    }
} 