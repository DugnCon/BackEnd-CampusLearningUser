package com.javaweb.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.javaweb.entity.Course.CourseModuleEntity;

public interface ICourseModulesRepository extends JpaRepository<CourseModuleEntity, Long>{
	@Query("select md from CourseModuleEntity md join fetch md.courses join fetch md.lessons where md.courses.CourseID = :courseId")
	Set<CourseModuleEntity> getModuleForLesson(@Param("courseId") Long courseId);
}
