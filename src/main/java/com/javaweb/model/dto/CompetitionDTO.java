package com.javaweb.model.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CompetitionDTO {
    private Long competitionID;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private String difficulty;
    private String status;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private BigDecimal prizePool;
    private Long organizedBy;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String coverImageURL;
    private boolean isRegistered;

    // Constructors
    public CompetitionDTO() {
    }

    public CompetitionDTO(Long competitionId, String title, String description, LocalDateTime startTime,
                          LocalDateTime endTime, Integer duration, String difficulty, String status,
                          Integer maxParticipants, Integer currentParticipants, BigDecimal prizePool,
                          Long organizedBy, String thumbnailUrl, LocalDateTime createdAt,
                          LocalDateTime updatedAt, String coverImageURL) {
        this.competitionID = competitionId;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.difficulty = difficulty;
        this.status = status;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
        this.prizePool = prizePool;
        this.organizedBy = organizedBy;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.coverImageURL = coverImageURL;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    // Getters and Setters
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

    public String getCoverImageURL() {
        return coverImageURL;
    }

    public void setCoverImageURL(String coverImageURL) {
        this.coverImageURL = coverImageURL;
    }
}
