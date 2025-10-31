package com.javaweb.repository;

import com.javaweb.entity.Coding.CodingExercisesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ICodingExerciseRepository extends JpaRepository<CodingExercisesEntity,Long> {
    @Query("select ce from CodingExercisesEntity ce join fetch ce.lessons where ce.lessons.LessonID = :lessonId")
    CodingExercisesEntity getCodingExercise(@Param("lessonId") Long lessonId);
}
