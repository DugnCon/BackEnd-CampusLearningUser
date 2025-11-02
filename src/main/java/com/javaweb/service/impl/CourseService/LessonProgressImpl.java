package com.javaweb.service.impl.CourseService;

import com.google.api.client.util.DateTime;
import com.javaweb.entity.Course.CourseEnrollmentEntity;
import com.javaweb.entity.Course.LessonProgressEntity;
import com.javaweb.repository.ICourseEnrollmentRepository;
import com.javaweb.repository.ILessonProgressRepository;
import com.javaweb.service.ILessonProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
@Service
public class LessonProgressImpl implements ILessonProgressService {
    @Autowired
    private ILessonProgressRepository lessonProgressRepository;
    @Autowired
    private ICourseEnrollmentRepository courseEnrollmentRepository;
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public ResponseEntity<Object> lessonCompleted(String status, Long lessonId, Long userId) {
        
        CourseEnrollmentEntity courseEnrollment = courseEnrollmentRepository.getCourseEnrollmentForProgress(userId);
        LessonProgressEntity progressEntity = lessonProgressRepository.updateStatusLessonProgress(courseEnrollment.getEnrollmentID(), lessonId);
        //return ResponseEntity.ok(progressEntity);
        //System.out.println(progressEntity);
        try {
            progressEntity.setStatus("completed");
            progressEntity.setCompletedAt(LocalDateTime.now());
            lessonProgressRepository.save(progressEntity);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã hoàn thành khóa học"));
        } catch (Exception e) {
            throw new RuntimeException(e + " error in adding lesson progress");
        }
    }
}
