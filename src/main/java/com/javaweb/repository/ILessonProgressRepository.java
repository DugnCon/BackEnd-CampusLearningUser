package com.javaweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.javaweb.entity.Course.LessonProgressEntity;

import java.util.Set;

@Repository
public interface ILessonProgressRepository extends JpaRepository<LessonProgressEntity, Long>{
    @Query("select lp from LessonProgressEntity lp join fetch lp.enrollment e join fetch lp.lessons where e.EnrollmentID = :enrollmentId")
    Set<LessonProgressEntity> getLessonProgress(@Param("enrollmentId") Long enrollmentId);
    @Query("select lp from LessonProgressEntity lp join fetch lp.enrollment e join fetch lp.lessons l where e.EnrollmentID = :enrollmentId and l.LessonID = :lessonId")
    LessonProgressEntity updateStatusLessonProgress(@Param("enrollmentId") Long enrollmentId, @Param("lessonId") Long lessonId);
}
