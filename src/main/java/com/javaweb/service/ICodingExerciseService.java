package com.javaweb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ICodingExerciseService {
    ResponseEntity<Object> getCodingExercise(Long lessonId);
    ResponseEntity<Object> submitCode(Map<String,Object> data, Long lessonId);
}
