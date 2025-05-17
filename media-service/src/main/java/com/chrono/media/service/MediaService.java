package com.chrono.media.service;

import java.util.List;

import com.chrono.media.dto.MediaRequest;
import com.chrono.media.dto.MediaResponse;

public interface MediaService {
    MediaResponse addMedia(MediaRequest request);
    List<MediaResponse> getMediaByEvent(Long eventId);
}
