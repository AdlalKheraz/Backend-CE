package com.chrono.media.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chrono.media.dto.MediaRequest;
import com.chrono.media.dto.MediaResponse;
import com.chrono.media.dto.MediaUploadRequest;
import com.chrono.media.service.MediaService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class MediaController {

    private static final String USER_ID_ATTRIBUTE = "userId";
    
    private final MediaService mediaService;

    @PostMapping
    public ResponseEntity<MediaResponse> addMedia(@RequestBody MediaRequest request, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(USER_ID_ATTRIBUTE);
        log.info("Ajout d'un média par l'utilisateur: {}", userId);
        
        MediaResponse response = mediaService.addMedia(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaResponse> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute MediaUploadRequest request,
            HttpServletRequest httpRequest) {
        
        String userId = (String) httpRequest.getAttribute(USER_ID_ATTRIBUTE);
        log.info("Upload d'un média par l'utilisateur: {}", userId);
        
        MediaResponse response = mediaService.uploadMedia(file, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<MediaResponse>> getAllMedia(HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(USER_ID_ATTRIBUTE);
        log.info("Récupération de tous les médias par l'utilisateur: {}", userId);
        
        List<MediaResponse> mediaList = mediaService.getAllMedia();
        return ResponseEntity.ok(mediaList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getMediaById(@PathVariable Long id, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(USER_ID_ATTRIBUTE);
        log.info("Récupération du média {} par l'utilisateur: {}", id, userId);
        
        MediaResponse media = mediaService.getMediaById(id);
        return ResponseEntity.ok(media);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<MediaResponse>> getMediaByEvent(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(USER_ID_ATTRIBUTE);
        log.info("Récupération des médias de l'événement: {} par l'utilisateur: {}", eventId, userId);
        
        List<MediaResponse> mediaList = mediaService.getMediaByEvent(eventId);
        return ResponseEntity.ok(mediaList);
    }
    
    @GetMapping("/event/{eventId}/uploaded")
    public ResponseEntity<List<MediaResponse>> getUploadedMediaByEvent(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(USER_ID_ATTRIBUTE);
        log.info("Récupération des médias téléchargés uniquement pour l'événement: {} par l'utilisateur: {}", eventId, userId);
        
        List<MediaResponse> mediaList = mediaService.getUploadedMediaByEvent(eventId);
        return ResponseEntity.ok(mediaList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long id, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(USER_ID_ATTRIBUTE);
        log.info("Suppression du média {} par l'utilisateur: {}", id, userId);
        
        mediaService.deleteMedia(id);
        return ResponseEntity.noContent().build();
    }
}
