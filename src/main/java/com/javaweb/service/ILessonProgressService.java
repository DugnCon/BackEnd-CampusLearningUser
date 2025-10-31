package com.javaweb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ILessonProgressService {
    ResponseEntity<Object> lessonCompleted(String status, Long lessonId, Long userId);
}
