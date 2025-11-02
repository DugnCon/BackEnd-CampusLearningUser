package com.javaweb.model.dto;
import java.time.LocalDateTime;

public class CompetitionRegistrationDTO {
    private Integer registrationID;
    private Long userID;
    private Long competitionID;
    private LocalDateTime registrationDate;
    private String status;
    private Integer score;
    private Integer problemsSolved;
    private Integer ranking;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public CompetitionRegistrationDTO() {
    }

    // Getters and Setters


    public Long getCompetitionID() {
        return competitionID;
    }

    public void setCompetitionID(Long competitionID) {
        this.competitionID = competitionID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Integer getRegistrationID() {
        return registrationID;
    }

    public void setRegistrationID(Integer registrationID) {
        this.registrationID = registrationID;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
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

    public Integer getProblemsSolved() {
        return problemsSolved;
    }

    public void setProblemsSolved(Integer problemsSolved) {
        this.problemsSolved = problemsSolved;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}