package com.javaweb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.javaweb.entity.UserEntity;
import com.javaweb.entity.Course.CourseEnrollmentEntity;
import com.javaweb.entity.Course.CourseEntity;
import com.javaweb.model.dto.TransactionIDDTO;
@Service
public interface IPaymentService {
	ResponseEntity<Object> confirmedPayment(TransactionIDDTO transactionDTO);
	ResponseEntity<Object> courseEnrolled(Long userId);
	ResponseEntity<Object> checkCourseEnrollment(Long courseId, Long userId);
	
	void insertCourseEnrollment(CourseEntity course, UserEntity user);
	void insertLessonProgress(CourseEntity course, UserEntity user, CourseEnrollmentEntity courseEnrollment);
}
