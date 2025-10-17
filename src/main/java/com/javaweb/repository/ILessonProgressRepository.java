package com.javaweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javaweb.entity.Course.LessonProgressEntity;
@Repository
public interface ILessonProgressRepository extends JpaRepository<LessonProgressEntity, Long>{

}
