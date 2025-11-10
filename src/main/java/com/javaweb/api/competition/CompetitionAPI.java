package com.javaweb.api.competition;

import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.service.ICompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/api")
public class CompetitionAPI {
    @Autowired
    private ICompetitionService competitionService;
  

    @GetMapping("/competitions")
    public Object getAllCompetitions() {
        return competitionService.getAllCompetition();
    }

    @GetMapping("/competitions/{id}")
    public Object getCompetitionDetails(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        
        return competitionService.getCompetitionDetails(id, userId);
    }

    @PostMapping("/competitions/{id}/register")
    public Object registerForCompetition(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return competitionService.registerForCompetition(id, userId);
    }

    @PostMapping("/competitions/{id}/start")
    public Object startCompetition(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return competitionService.startCompetition(id, userId);
    }

    @GetMapping("/competitions/{id}/scoreboard")
    public Object getScoreboard(@PathVariable Long id) {
        return competitionService.getCompetitionScoreboard(id);
    }

    @GetMapping("/competitions/{competitionId}/problems/{problemId}")
    public Object getProblemDetails(@PathVariable Long competitionId, @PathVariable Long problemId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return competitionService.getProblemDetails(competitionId, problemId, userId);
    }

    @PostMapping("/competitions/{competitionId}/problems/{problemId}/submit")
    public Object submitSolution(@PathVariable Long competitionId, @PathVariable Long problemId, @RequestBody Map<String,Object> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return competitionService.submitSolution(competitionId, problemId, userId, request);
    }

    @GetMapping("/competitions/submissions/{submissionId}")
    public Object getSubmissionDetails(@PathVariable Long submissionId) {
        return competitionService.getSubmissionDetails(submissionId);
    }

    @GetMapping("/user/competitions")
    public Object getUserCompetitions() {
        return null;
    }
}