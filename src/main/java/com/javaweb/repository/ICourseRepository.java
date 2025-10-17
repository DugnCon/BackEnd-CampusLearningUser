package com.javaweb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.javaweb.entity.Course.CourseEntity;

@Repository
public interface ICourseRepository extends JpaRepository<CourseEntity, Long>{
	//@Procedure(name = "CourseEntity.getAllCourse")
	//List<CourseEntity> getAllCourse();
    @Query("select c from CourseEntity c")
    List<CourseEntity> getAllCourse();
    @Query("select c from CourseEntity c join fetch c.Modules m")
    CourseEntity getCourseById(@Param("courseId") Long courseId);
}
