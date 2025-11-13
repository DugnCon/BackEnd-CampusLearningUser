package com.javaweb.model.dto.User;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
public class UserProfileDTO {
    @JsonProperty("FullName")
    private String fullName;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Username")
    private String username;

    @JsonProperty("Bio")
    private String bio; // Thuộc UserEntity


    @JsonProperty("School")
    private String school;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("PhoneNumber")
    private String phoneNumber;

    @JsonProperty("DateOfBirth")
    private String dateOfBirth;

    @JsonProperty("SocialLinks")
    private Map<String, String> socialLinks;

    @JsonProperty("Education")
    private List<Map<String, String>> education;

    @JsonProperty("WorkExperience")
    private List<Map<String, String>> workExperience;

    @JsonProperty("Skills")
    private List<String> skills;

    @JsonProperty("Interests")
    private List<String> interests;

    @JsonProperty("Achievements")
    private List<String> achievements;

    // Cài đặt cá nhân
    @JsonProperty("PreferredLanguage")
    private String preferredLanguage;

    @JsonProperty("TimeZone")
    private String timeZone;

    @JsonProperty("NotificationPreferences")
    private Map<String, Boolean> notificationPreferences;

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSchool() {
        return school;
    }
    public void setSchool(String school) {
        this.school = school;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // II. Getters/Setters cho UserProfile Fields

    public Map<String, String> getSocialLinks() {
        return socialLinks;
    }
    public void setSocialLinks(Map<String, String> socialLinks) {
        this.socialLinks = socialLinks;
    }

    public List<Map<String, String>> getEducation() {
        return education;
    }
    public void setEducation(List<Map<String, String>> education) {
        this.education = education;
    }

    public List<Map<String, String>> getWorkExperience() {
        return workExperience;
    }
    public void setWorkExperience(List<Map<String, String>> workExperience) {
        this.workExperience = workExperience;
    }

    public List<String> getSkills() {
        return skills;
    }
    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getInterests() {
        return interests;
    }
    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<String> getAchievements() {
        return achievements;
    }
    public void setAchievements(List<String> achievements) {
        this.achievements = achievements;
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

    public Map<String, Boolean> getNotificationPreferences() {
        return notificationPreferences;
    }
    public void setNotificationPreferences(Map<String, Boolean> notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }

    // Trong file UserProfileDTO.java (sau các khai báo trường)



// ... (Các getters/setters và methods khác của DTO)

// Kết thúc class UserProfileDTO
}