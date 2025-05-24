package com.chrono.media.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chrono.media.dto.MediaRequest;
import com.chrono.media.dto.MediaResponse;
import com.chrono.media.dto.MediaUploadRequest;
import com.chrono.media.entity.Media;
import com.chrono.media.exception.MediaNotFoundException;
import com.chrono.media.repository.MediaRepository;
import com.chrono.media.service.MediaService;
import com.chrono.media.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final StorageService storageService;

    @Override
    public MediaResponse addMedia(MediaRequest request) {
        log.info("Ajout d'un nouveau média par URL pour l'événement: {}", request.getEventId());
        
        Media media = Media.builder()
                .url(request.getUrl())
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .eventId(request.getEventId())
                .build();

        Media savedMedia = mediaRepository.save(media);
        log.info("Média ajouté avec succès avec l'ID: {}", savedMedia.getId());
        
        return toMediaResponse(savedMedia);
    }

    @Override
    public MediaResponse uploadMedia(MultipartFile file, MediaUploadRequest request) {
        log.info("Upload d'un fichier média pour l'événement: {}", request.getEventId());
        
        String fileUrl = storageService.store(file);
        
        Media media = Media.builder()
                .url(fileUrl)
                .title(file.getOriginalFilename())
                .description("Fichier uploadé: " + file.getOriginalFilename())
                .type(request.getType())
                .eventId(request.getEventId())
                .build();

        Media savedMedia = mediaRepository.save(media);
        log.info("Fichier média uploadé avec succès avec l'ID: {}", savedMedia.getId());
        
        return toMediaResponse(savedMedia);
    }

    @Override
    public List<MediaResponse> getMediaByEvent(Long eventId) {
        log.info("Récupération des médias pour l'événement: {}", eventId);
        
        List<Media> mediaList = mediaRepository.findByEventId(eventId);
        
        if (mediaList.isEmpty()) {
            log.info("Aucun média trouvé pour l'événement: {}", eventId);
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} média(s) pour l'événement: {}", mediaList.size(), eventId);
        return mediaList.stream()
                .map(this::toMediaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MediaResponse> getUploadedMediaByEvent(Long eventId) {
        log.info("Récupération des médias uploadés pour l'événement: {}", eventId);
        
        List<Media> uploadedMediaList = mediaRepository.findUploadedMediaByEventId(eventId);
        
        if (uploadedMediaList.isEmpty()) {
            log.info("Aucun média uploadé trouvé pour l'événement: {}", eventId);
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} média(s) uploadé(s) pour l'événement: {}", uploadedMediaList.size(), eventId);
        return uploadedMediaList.stream()
                .map(this::toMediaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MediaResponse> getAllMedia() {
        log.info("Récupération de tous les médias");
        
        List<Media> allMedia = mediaRepository.findAll();
        
        if (allMedia.isEmpty()) {
            log.info("Aucun média trouvé dans la base de données");
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} média(s) au total", allMedia.size());
        return allMedia.stream()
                .map(this::toMediaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MediaResponse getMediaById(Long id) {
        log.info("Récupération du média avec l'ID: {}", id);
        
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Média non trouvé avec l'ID: " + id));
        
        return toMediaResponse(media);
    }

    @Override
    public void deleteMedia(Long id) {
        log.info("Suppression du média avec l'ID: {}", id);
        
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Média non trouvé avec l'ID: " + id));
        
        // Supprimer le fichier du stockage si c'est un fichier uploadé
        if (media.getUrl().startsWith("/api/media/files/")) {
            try {
                storageService.delete(media.getUrl());
                log.info("Fichier supprimé du stockage: {}", media.getUrl());
            } catch (Exception e) {
                log.warn("Erreur lors de la suppression du fichier: {}", e.getMessage());
            }
        }
        
        mediaRepository.deleteById(id);
        log.info("Média supprimé avec succès");
    }

    @Override
    public void deleteMediaByEvent(Long eventId) {
        log.info("Suppression de tous les médias pour l'événement: {}", eventId);
        
        List<Media> mediaList = mediaRepository.findByEventId(eventId);
        
        if (mediaList.isEmpty()) {
            log.info("Aucun média trouvé pour l'événement: {}", eventId);
            return;
        }
        
        log.info("Trouvé {} média(s) à supprimer pour l'événement: {}", mediaList.size(), eventId);
        
        // Supprimer les fichiers stockés si ce sont des fichiers uploadés
        for (Media media : mediaList) {
            if (media.getUrl().startsWith("/api/media/files/")) {
                try {
                    storageService.delete(media.getUrl());
                    log.info("Fichier supprimé du stockage: {}", media.getUrl());
                } catch (Exception e) {
                    log.warn("Erreur lors de la suppression du fichier: {}", e.getMessage());
                }
            }
        }
        
        // Supprimer tous les médias de l'événement de la base de données
        mediaRepository.deleteAll(mediaList);
        log.info("{} média(s) supprimé(s) avec succès pour l'événement: {}", mediaList.size(), eventId);
    }

    private MediaResponse toMediaResponse(Media media) {
        return MediaResponse.builder()
                .id(media.getId())
                .url(media.getUrl())
                .title(media.getTitle())
                .description(media.getDescription())
                .type(media.getType())
                .eventId(media.getEventId())
                .build();
    }
}
