package com.chrono.media.repository;

import com.chrono.media.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByEventId(Long eventId);
}
