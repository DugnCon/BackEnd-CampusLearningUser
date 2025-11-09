package com.javaweb.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "userprofiles")
public class UserProfile {

    // Helper static final ObjectMapper để xử lý JSON
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ... (Các trường fields, Constructor, và Getters/Setters cơ bản giữ nguyên)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProfileID")
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "Education", columnDefinition = "TEXT")
    private String educationJson;

    // ... các trường JSON khác (WorkExperienceJson, SkillsJson, InterestsJson, SocialLinksJson, AchievementsJson, NotificationPreferencesJson) ...

    @Column(name = "WorkExperience", columnDefinition = "TEXT")
    private String workExperienceJson;

    @Column(name = "Skills", columnDefinition = "TEXT")
    private String skillsJson;

    @Column(name = "Interests", columnDefinition = "TEXT")
    private String interestsJson;

    @Column(name = "SocialLinks", columnDefinition = "TEXT")
    private String socialLinksJson;

    @Column(name = "Achievements", columnDefinition = "TEXT")
    private String achievementsJson;

    @Column(name = "PreferredLanguage", length = 10)
    private String preferredLanguage;

    @Column(name = "TimeZone", length = 50)
    private String timeZone;

    @Column(name = "NotificationPreferences", columnDefinition = "TEXT")
    private String notificationPreferencesJson;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", updatable = false)
    private LocalDateTime updatedAt;



    public UserProfile() {
        this.preferredLanguage = "vi";
        this.timeZone = "Asia/Ho_Chi_Minh";
    }

    // Helper chung để chuyển Object sang JSON String (không cần thay đổi)
    private String convertObjectToJsonString(Object object) throws JsonProcessingException {
        if (object == null || (object instanceof List && ((List) object).isEmpty()) || (object instanceof Map && ((Map) object).isEmpty()))
            return null;
        return objectMapper.writeValueAsString(object);
    }

    // Helper chung để đọc JSON Array (cho Education, WorkExperience,...)
    private <T> List<T> getListFromJson(String json, TypeReference<List<T>> typeRef) throws IOException {
        if (json == null || json.isEmpty() || json.equals("null")) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            System.err.println("Warning: Corrupt List data in DB. Returning empty List.");
            return Collections.emptyList(); // Bắt lỗi và trả về List rỗng
        }
    }

    // Helper chung để đọc JSON Map (cho SocialLinks, NotificationPreferences,...)
    private <K, V> Map<K, V> getMapFromJson(String json, TypeReference<Map<K, V>> typeRef) throws IOException {
        if (json == null || json.isEmpty() || json.equals("null")) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            // Bắt lỗi nếu dữ liệu là Array hoặc lỗi cú pháp Map, và trả về Map rỗng
            System.err.println("Warning: Corrupt Map data in DB. Expected Map, but JSON structure is incompatible. Returning empty Map.");
            return Collections.emptyMap();
        }
    }

    // --- Getters/Setters Cụ thể cho JSON Fields (ĐÃ SỬA LỖI) ---

    // 🔴 Lỗi xảy ra ở đây: SocialLinks (Map)
    public Map<String, String> getSocialLinksMap() throws IOException {
        return getMapFromJson(this.socialLinksJson, new TypeReference<Map<String, String>>() {});
    }
    public void setSocialLinksMap(Map<String, String> socialLinks) throws JsonProcessingException {
        this.socialLinksJson = convertObjectToJsonString(socialLinks);
    }

    // Education (List)
    public List<Map<String, String>> getEducationList() throws IOException {
        return getListFromJson(this.educationJson, new TypeReference<List<Map<String, String>>>() {});
    }
    public void setEducationList(List<Map<String, String>> education) throws JsonProcessingException {
        this.educationJson = convertObjectToJsonString(education);
    }

    // WorkExperience (List)
    public List<Map<String, String>> getWorkExperienceList() throws IOException {
        return getListFromJson(this.workExperienceJson, new TypeReference<List<Map<String, String>>>() {});
    }
    public void setWorkExperienceList(List<Map<String, String>> workExperience) throws JsonProcessingException {
        this.workExperienceJson = convertObjectToJsonString(workExperience);
    }

    // Skills (List)
    public List<String> getSkillsList() throws IOException {
        return getListFromJson(this.skillsJson, new TypeReference<List<String>>() {});
    }
    public void setSkillsList(List<String> skills) throws JsonProcessingException {
        this.skillsJson = convertObjectToJsonString(skills);
    }

    // ... (Các Getters/Setters JSON còn lại tương tự) ...
    public List<String> getInterestsList() throws IOException {
        return getListFromJson(this.interestsJson, new TypeReference<List<String>>() {});
    }
    public void setInterestsList(List<String> interests) throws JsonProcessingException {
        this.interestsJson = convertObjectToJsonString(interests);
    }

    public List<String> getAchievementsList() throws IOException {
        return getListFromJson(this.achievementsJson, new TypeReference<List<String>>() {});
    }
    public void setAchievementsList(List<String> achievements) throws JsonProcessingException {
        this.achievementsJson = convertObjectToJsonString(achievements);
    }

    public Map<String, Boolean> getNotificationPreferencesMap() throws IOException {
        return getMapFromJson(this.notificationPreferencesJson, new TypeReference<Map<String, Boolean>>() {});
    }
    public void setNotificationPreferencesMap(Map<String, Boolean> preferences) throws JsonProcessingException {
        this.notificationPreferencesJson = convertObjectToJsonString(preferences);
    }

    // --- Getters/Setters Cơ bản (giữ nguyên) ---

    // ... (Các Getters/Setters cho ProfileID, User, TimeZone, PreferredLanguage, UpdatedAt) ...
    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }
    // ...
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
    // ...
}