package com.javaweb.repository;

import com.javaweb.entity.Story.StoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IStoryRepository extends JpaRepository<StoryEntity, Long> {
    @Query("SELECT s FROM StoryEntity s WHERE s.expiresAt > :now ORDER BY s.createdAt DESC")
    List<StoryEntity> findActiveStories(@Param("now") LocalDateTime now);
}
