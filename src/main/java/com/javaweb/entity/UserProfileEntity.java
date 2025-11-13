package com.javaweb.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.converter.JsonConverter;
import com.javaweb.model.dto.Profile.ProfileInformation.EducationDTO;
import com.javaweb.model.dto.Profile.ProfileInformation.SocialLinkDTO;
import com.javaweb.model.dto.Profile.ProfileInformation.WorkExperienceDTO;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "userprofiles")
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProfileID")
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "Education")
    @Convert(converter = JsonConverter.class)
    private List<EducationDTO> education;

    @Column(name = "WorkExperience")
    @Convert(converter = JsonConverter.class)
    private List<WorkExperienceDTO> workExperience;

    @Column(name = "Skills")
    @Convert(converter = JsonConverter.class)
    private List<String> skills;

    @Column(name = "Interests")
    @Convert(converter = JsonConverter.class)
    private List<String> interests;

    @Column(name = "SocialLinks")
    @Convert(converter = JsonConverter.class)
    private SocialLinkDTO socialLinks;

    @Column(name = "Achievements")
    private String achievements;

    @Column(name = "PreferredLanguage", length = 10)
    private String preferredLanguage;

    @Column(name = "TimeZone", length = 50)
    private String timeZone;

    @Column(name = "NotificationPreferences")
    private String notificationPreferencesJson;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", updatable = false)
    private LocalDateTime updatedAt;

    public UserProfileEntity() {
        this.preferredLanguage = "vi";
        this.timeZone = "Asia/Ha_Noi";
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public SocialLinkDTO getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(SocialLinkDTO socialLinks) {
        this.socialLinks = socialLinks;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<WorkExperienceDTO> getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(List<WorkExperienceDTO> workExperience) {
        this.workExperience = workExperience;
    }

    public List<EducationDTO> getEducation() {
        return education;
    }

    public void setEducation(List<EducationDTO> education) {
        this.education = education;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNotificationPreferencesJson() {
        return notificationPreferencesJson;
    }

    public void setNotificationPreferencesJson(String notificationPreferencesJson) {
        this.notificationPreferencesJson = notificationPreferencesJson;
    }
}