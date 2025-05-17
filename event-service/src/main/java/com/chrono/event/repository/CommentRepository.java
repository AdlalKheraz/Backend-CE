package com.chrono.event.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chrono.event.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventId(Long eventId);
}
