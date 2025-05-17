package com.chrono.media.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrono.media.dto.MediaRequest;
import com.chrono.media.dto.MediaResponse;
import com.chrono.media.service.MediaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping
    public MediaResponse addMedia(@RequestBody MediaRequest request) {
        return mediaService.addMedia(request);
    }

    @GetMapping("/event/{eventId}")
    public List<MediaResponse> getMediaByEvent(@PathVariable Long eventId) {
        return mediaService.getMediaByEvent(eventId);
    }
}
