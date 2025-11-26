package com.javaweb.repository;

import com.javaweb.entity.Course.CourseLessonsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICourseLessonRepository extends JpaRepository<CourseLessonsEntity, Long> {
}
