package com.chrono.media.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.chrono.media.dto.MediaRequest;
import com.chrono.media.dto.MediaResponse;
import com.chrono.media.entity.Media;
import com.chrono.media.repository.MediaRepository;
import com.chrono.media.service.MediaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

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
    public List<MediaResponse> getMediaByEvent(Long eventId) {
        return mediaRepository.findByEventId(eventId)
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
