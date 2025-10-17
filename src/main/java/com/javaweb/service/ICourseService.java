package com.javaweb.service;

import com.javaweb.entity.Course.CourseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ICourseService {
    List<CourseEntity> getAllCourse();
    CourseEntity getCourseById(Long courseId);
    double getCoursePrice(Long courseId);
    ResponseEntity<Object> getUserProgress(Long courseId, Long userId);
}
