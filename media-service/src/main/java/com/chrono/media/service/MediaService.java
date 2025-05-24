package com.chrono.media.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.chrono.media.dto.MediaRequest;
import com.chrono.media.dto.MediaResponse;
import com.chrono.media.dto.MediaUploadRequest;

public interface MediaService {
    MediaResponse addMedia(MediaRequest request);
    MediaResponse uploadMedia(MultipartFile file, MediaUploadRequest request);
    List<MediaResponse> getMediaByEvent(Long eventId);
    List<MediaResponse> getUploadedMediaByEvent(Long eventId);
    List<MediaResponse> getAllMedia();
    MediaResponse getMediaById(Long id);
    void deleteMedia(Long id);
    void deleteMediaByEvent(Long eventId);
}
