package com.chrono.media.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chrono.media.entity.Media;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByEventId(Long eventId);
    
    @Query("SELECT m FROM Media m WHERE m.eventId = :eventId AND m.url LIKE '/api/media/files/%'")
    List<Media> findUploadedMediaByEventId(@Param("eventId") Long eventId);
}
