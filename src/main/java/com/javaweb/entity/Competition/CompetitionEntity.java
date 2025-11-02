package com.javaweb.entity.Competition;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "competitions")
public class CompetitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CompetitionID")
    private Long competitionID;

    @Column(name = "Title", nullable = false, length = 200)
    private String title;

    @Column(name = "Description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "StartTime", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "Duration", nullable = false)
    private Integer duration;

    @Column(name = "Difficulty", nullable = false, length = 20)
    private String difficulty = "Trung bình";

    @Column(name = "Status", nullable = false, length = 20)
    private String status = "draft";

    @Column(name = "MaxParticipants", nullable = false)
    private Integer maxParticipants = 100;

    @Column(name = "CurrentParticipants", nullable = false)
    private Integer currentParticipants = 0;

    @Column(name = "PrizePool", nullable = false, precision = 12, scale = 2)
    private BigDecimal prizePool = BigDecimal.ZERO;

    //user
    @Column(name = "OrganizedBy")
    private Long organizedBy;

    @Column(name = "ThumbnailUrl", length = 500)
    private String thumbnailUrl;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;

    @Column(name = "CoverImageURL", length = 500)
    private String coverImageURL;

    @OneToMany(mappedBy = "competition", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CompetitionRegistrationEntity> registration;

    @OneToMany(mappedBy = "competition", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CompetitionParticipantEntity> participant;

    @OneToMany(mappedBy = "competition", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CompetitionProblemEntity> problem;

    public CompetitionEntity() {
    }

    // Getters and Setters
    @JsonBackReference
    public List<CompetitionProblemEntity> getProblem() {
        return problem;
    }

    public void setProblem(List<CompetitionProblemEntity> problem) {
        this.problem = problem;
    }

    @JsonBackReference
    public List<CompetitionParticipantEntity> getParticipant() {
        return participant;
    }

    public void setParticipant(List<CompetitionParticipantEntity> participant) {
        this.participant = participant;
    }

    @JsonBackReference
    public List<CompetitionRegistrationEntity> getRegistration() {
        return registration;
    }

    public void setRegistration(List<CompetitionRegistrationEntity> registration) {
        this.registration = registration;
    }

    public Long getCompetitionID() {
        return competitionID;
    }

    public void setCompetitionID(Long competitionID) {
        this.competitionID = competitionID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public BigDecimal getPrizePool() {
        return prizePool;
    }

    public void setPrizePool(BigDecimal prizePool) {
        this.prizePool = prizePool;
    }

    public Long getOrganizedBy() {
        return organizedBy;
    }

    public void setOrganizedBy(Long organizedBy) {
        this.organizedBy = organizedBy;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getCoverImageURL() {
        return coverImageURL;
    }

    public void setCoverImageURL(String coverImageURL) {
        this.coverImageURL = coverImageURL;
    }
}