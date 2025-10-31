package com.javaweb.service.impl.CourseService;

import com.javaweb.entity.Coding.CodingExercisesEntity;
import com.javaweb.model.dto.TestCasesDTO;
import com.javaweb.repository.ICodingExerciseRepository;
import com.javaweb.service.ICodingExerciseService;
import com.javaweb.service.Judge0Service;
import org.aspectj.weaver.ast.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class CodingExerciseServiceImpl implements ICodingExerciseService {
    @Autowired
    private ICodingExerciseRepository codingExerciseRepository;
    @Autowired
    private Judge0Service judge0Service;
    @Override
    public ResponseEntity<Object> getCodingExercise(Long lessonId) {
        try {
            CodingExercisesEntity codingExercisesEntity = codingExerciseRepository.getCodingExercise(lessonId);
            return ResponseEntity.ok(Map.of("success", true, "data", codingExercisesEntity));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public ResponseEntity<Object> submitCode(Map<String, Object> data, Long lessonId) {
        String code = ((Map<String, Object>) data.get("code")).get("code").toString();
        CodingExercisesEntity codingExercisesEntity = codingExerciseRepository.getCodingExercise(lessonId);
        List<TestCasesDTO> testCases = codingExercisesEntity.getTestCases();

        List<Map<String, Object>> result = judge0Service.submitMultipleTestCases(code, 54, testCases);

        boolean allPassed = result.stream()
                .allMatch(submission -> "Accepted".equals(submission.get("status")));

        // chỉ trả về success + passed
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of("passed", allPassed)
        ));
    }

}
