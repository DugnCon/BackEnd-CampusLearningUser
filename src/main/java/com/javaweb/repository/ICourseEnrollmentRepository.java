package com.javaweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.javaweb.entity.Course.CourseEnrollmentEntity;

import java.util.List;
import java.util.Set;

@Repository
public interface ICourseEnrollmentRepository extends JpaRepository<CourseEnrollmentEntity, Long>{
	/**
	 * findByA_B
	 * Đi sâu từ entity hiện tại (this) đến entity liên kết (A), rồi truy cập thuộc tính (B).
	 * **/
	//boolean existsByCourseEnrollment_CourseID(Long courseId);
	@Query("select ce from CourseEnrollmentEntity ce join fetch ce.courseEnrollment join fetch ce.userEnrollment where ce.courseEnrollment.CourseID = :courseId and ce.userEnrollment.UserID = :userId")
	CourseEnrollmentEntity checkCourseEnrollment(@Param("courseId") Long courseId, @Param("userId") Long userId);
	@Query("select ce from CourseEnrollmentEntity ce join fetch ce.courseEnrollment join fetch ce.userEnrollment where ce.courseEnrollment.CourseID = :courseId and ce.userEnrollment.UserID = :userId")
	CourseEnrollmentEntity getUserProgress(@Param("courseId") Long courseId, @Param("userId") Long userId);
	@Query("SELECT CASE WHEN COUNT(ce) > 0 THEN true ELSE false END FROM CourseEnrollmentEntity ce WHERE ce.userEnrollment.UserID = :userId")
	boolean existsUserInEnrollment(@Param("userId") Long userId);
	@Query("select ce from CourseEnrollmentEntity ce join fetch ce.userEnrollment where ce.userEnrollment.UserID = :userId")
	CourseEnrollmentEntity getCourseEnrollmentForProgress(@Param("userId") Long userId);
	@Query("select ce from CourseEnrollmentEntity ce join fetch ce.userEnrollment join fetch ce.courseEnrollment c  where ce.userEnrollment.UserID = :userId")
	Set<CourseEnrollmentEntity> getUserCourseEnrolled(@Param("userId") Long userId);
}
