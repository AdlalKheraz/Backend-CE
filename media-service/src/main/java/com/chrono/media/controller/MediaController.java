package com.chrono.media.controller;

import java.util.List;

import org.springframework.http.MediaType;
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
    public MediaResponse addMedia(@RequestBody MediaRequest request, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(USER_ID_ATTRIBUTE);
        log.info("Ajout d'un média par l'utilisateur: {}", userId);
        
        return mediaService.addMedia(request);
    }
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MediaResponse uploadMedia(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute MediaUploadRequest request,
            HttpServletRequest httpRequest) {
        
        String userId = (String) httpRequest.getAttribute(USER_ID_ATTRIBUTE);
        log.info("Upload d'un média par l'utilisateur: {}", userId);
        
        return mediaService.uploadMedia(file, request);
    }

    @GetMapping("/event/{eventId}")
    public List<MediaResponse> getMediaByEvent(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(USER_ID_ATTRIBUTE);
        log.info("Récupération des médias de l'événement: {} par l'utilisateur: {}", eventId, userId);
        
        return mediaService.getMediaByEvent(eventId);
    }
    
    @GetMapping("/event/{eventId}/uploaded")
    public List<MediaResponse> getUploadedMediaByEvent(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(USER_ID_ATTRIBUTE);
        log.info("Récupération des médias téléchargés uniquement pour l'événement: {} par l'utilisateur: {}", eventId, userId);
        
        return mediaService.getUploadedMediaByEvent(eventId);
    }
}
