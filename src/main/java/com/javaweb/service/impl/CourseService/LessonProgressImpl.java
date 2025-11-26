package com.javaweb.service.impl.CourseService;

import com.google.api.client.util.DateTime;
import com.javaweb.entity.Course.CourseEnrollmentEntity;
import com.javaweb.entity.Course.CourseLessonsEntity;
import com.javaweb.entity.Course.LessonProgressEntity;
import com.javaweb.repository.ICourseEnrollmentRepository;
import com.javaweb.repository.ICourseLessonRepository;
import com.javaweb.repository.ILessonProgressRepository;
import com.javaweb.service.ILessonProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LessonProgressImpl implements ILessonProgressService {
    @Autowired
    private ILessonProgressRepository lessonProgressRepository;
    @Autowired
    private ICourseLessonRepository courseLessonRepository;
    @Autowired
    private ICourseEnrollmentRepository courseEnrollmentRepository;
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)

    public ResponseEntity<Object> lessonCompleted(String status, Long lessonId, Long userId) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            CourseEnrollmentEntity courseEnrollment = courseEnrollmentRepository.getCourseEnrollmentForProgress(userId);

            Long enrollmentId = courseEnrollment.getEnrollmentID();

            CompletableFuture<CourseLessonsEntity> courseLessonProgressEntityAsync = CompletableFuture.supplyAsync(() -> getLessonProgress(lessonId), executorService);
            CompletableFuture<CourseEnrollmentEntity> courseEnrollmentEntityAsync = CompletableFuture.supplyAsync(() -> getCourseEnrollment(enrollmentId), executorService);
            CompletableFuture.allOf(courseLessonProgressEntityAsync, courseEnrollmentEntityAsync).join();

            LessonProgressEntity progressEntity = new LessonProgressEntity();
            progressEntity.setLessons(courseLessonProgressEntityAsync.join());
            progressEntity.setEnrollment(courseEnrollmentEntityAsync.join());

            progressEntity.setStatus("completed");
            progressEntity.setCompletedAt(LocalDateTime.now());
            lessonProgressRepository.save(progressEntity);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã hoàn thành khóa học"));
        } catch (Exception e) {
            throw new RuntimeException(e + " error in adding lesson progress");
        }
    }
    public CourseLessonsEntity getLessonProgress(Long lessonId) {
        return courseLessonRepository.findById(lessonId).orElseThrow(() -> new RuntimeException("not found lesson"));
    }
    public CourseEnrollmentEntity getCourseEnrollment(Long enrollmentId) {
        return courseEnrollmentRepository.findById(enrollmentId).orElseThrow(() -> new RuntimeException("not found course enrollment"));
    }
}