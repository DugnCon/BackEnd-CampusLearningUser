package com.javaweb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

public interface ICompetitionService {
    ResponseEntity<Object> getAllCompetition();
    ResponseEntity<Object> getCompetitionDetails(Long competitionId, Long userId);
    ResponseEntity<Object> registerForCompetition(Long competitionId, Long userId);
    ResponseEntity<Object> startCompetition(Long competitionId, Long userId);
    ResponseEntity<Object> getCompetitionProblems(Long competitionId, Long userId);
    ResponseEntity<Object> getProblemDetails(Long competitionId, Long problemId, Long userId);
    ResponseEntity<Object> submitSolution(Long competitionId, Long problemId, Long userId, Map<String, Object> requestData);
    ResponseEntity<Object> getSubmissionDetails(Long submissionId, Long userId);
    ResponseEntity<Object> getSubmissionDetails(Long submissionId);
    ResponseEntity<Object> getCompetitionScoreboard(Long id);
}
