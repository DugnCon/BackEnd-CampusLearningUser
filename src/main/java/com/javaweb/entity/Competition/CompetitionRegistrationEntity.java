package com.javaweb.entity.Competition;

import com.javaweb.entity.UserEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "competitionregistrations")
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class CompetitionRegistrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RegistrationID")
    private Integer registrationID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitionID")
    private CompetitionEntity competition;

    @Column(name = "RegistrationDate", nullable = false)
    @CreationTimestamp
    private LocalDateTime registrationDate;

    @Column(name = "Status", length = 20)
    private String status;

    @Column(name = "Score")
    private Integer score;

    @Column(name = "ProblemsSolved")
    private Integer problemsSolved;

    @Column(name = "Ranking")
    private Integer ranking;

    @Column(name = "CreatedAt", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public CompetitionRegistrationEntity() {
    }

    // Getters and Setters
    public Integer getRegistrationID() {
        return registrationID;
    }

    public void setRegistrationID(Integer registrationID) {
        this.registrationID = registrationID;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CompetitionEntity getCompetition() {
        return competition;
    }

    public void setCompetition(CompetitionEntity competition) {
        this.competition = competition;
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