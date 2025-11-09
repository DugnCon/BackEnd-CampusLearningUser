package com.javaweb.entity.Competition;

import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "competitionsubmissions")
public class CompetitionSubmissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SubmissionID")
    private Long submissionID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problemID", nullable = false)
    private CompetitionProblemEntity problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participantID", nullable = false)
    private CompetitionParticipantEntity participant;

    @Column(name = "SourceCode", nullable = false, columnDefinition = "TEXT")
    private String sourceCode;

    @Column(name = "Language", nullable = false, length = 50)
    private String language;

    @Column(name = "Status", nullable = false, length = 50)
    private String status = "pending";

    @Column(name = "Score", nullable = false)
    private Integer score = 0;

    @Column(name = "ExecutionTime", precision = 10, scale = 3)
    private BigDecimal executionTime;

    @Column(name = "MemoryUsed")
    private Integer memoryUsed;

    @Column(name = "ErrorMessage", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "SubmittedAt", nullable = false)
    @CreationTimestamp
    private LocalDateTime submittedAt;

    @Column(name = "JudgedAt")
    private LocalDateTime judgedAt;

    public CompetitionSubmissionEntity() {
    }

    // Getters and Setters

    public Long getSubmissionID() {
        return submissionID;
    }

    public void setSubmissionID(Long submissionID) {
        this.submissionID = submissionID;
    }

    public CompetitionProblemEntity getProblem() {
        return problem;
    }

    public void setProblem(CompetitionProblemEntity problem) {
        this.problem = problem;
    }

    public CompetitionParticipantEntity getParticipant() {
        return participant;
    }

    public void setParticipant(CompetitionParticipantEntity participant) {
        this.participant = participant;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public BigDecimal getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(BigDecimal executionTime) {
        this.executionTime = executionTime;
    }

    public Integer getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(Integer memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getJudgedAt() {
        return judgedAt;
    }

    public void setJudgedAt(LocalDateTime judgedAt) {
        this.judgedAt = judgedAt;
    }
}