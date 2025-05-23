package com.chrono.media.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chrono.media.dto.MediaRequest;
import com.chrono.media.dto.MediaResponse;
import com.chrono.media.dto.MediaUploadRequest;
import com.chrono.media.entity.Media;
import com.chrono.media.repository.MediaRepository;
import com.chrono.media.service.MediaService;
import com.chrono.media.service.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final StorageService storageService;

    @Override
    public MediaResponse addMedia(MediaRequest request) {
        Media media = Media.builder()
                .url(request.getUrl())
                .type(request.getType())
                .eventId(request.getEventId())
                .build();
        Media saved = mediaRepository.save(media);
        return toResponse(saved);
    }
    
    @Override
    public MediaResponse uploadMedia(MultipartFile file, MediaUploadRequest request) {
        // Vérifier que le type de média est cohérent avec le fichier
        String contentType = file.getContentType();
        if (contentType != null) {
            if (request.getType() == com.chrono.media.entity.MediaType.IMAGE 
                    && !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Le fichier n'est pas une image");
            } else if (request.getType() == com.chrono.media.entity.MediaType.VIDEO 
                    && !contentType.startsWith("video/")) {
                throw new IllegalArgumentException("Le fichier n'est pas une vidéo");
            }
        }
        
        // Stocker le fichier
        String fileUrl = storageService.store(file);
        
        // Créer le média en base de données
        Media media = Media.builder()
                .url(fileUrl)
                .type(request.getType())
                .eventId(request.getEventId())
                .build();
        
        Media saved = mediaRepository.save(media);
        return toResponse(saved);
    }

    @Override
    public List<MediaResponse> getMediaByEvent(Long eventId) {
        return mediaRepository.findByEventId(eventId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MediaResponse> getUploadedMediaByEvent(Long eventId) {
        return mediaRepository.findUploadedMediaByEventId(eventId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MediaResponse> getAllMedia() {
        return mediaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MediaResponse toResponse(Media media) {
        return MediaResponse.builder()
                .id(media.getId())
                .url(media.getUrl())
                .type(media.getType())
                .eventId(media.getEventId())
                .build();
    }
}
