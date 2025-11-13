package com.javaweb.model.dto.Profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.javaweb.model.dto.Profile.ProfileInformation.EducationDTO;
import com.javaweb.model.dto.Profile.ProfileInformation.SocialLinkDTO;
import com.javaweb.model.dto.Profile.ProfileInformation.WorkExperienceDTO;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDetailDTO {

    // Basic Info
    private String fullName;
    private String email;
    private String username;
    private String bio;
    private String pronouns;
    private String url;
    private String orcidId;

    // Location & Contact
    private String school;
    private String address;
    private String city;
    private String country;
    private String phoneNumber;
    private String dateOfBirth;

    // References to other DTOs
    private SocialLinkDTO socialLinks;
    private List<EducationDTO> education;
    private List<WorkExperienceDTO> workExperience;
    private List<String> skills;
    private List<String> interests;
    private List<String> achievements;

    // Settings
    private String preferredLanguage;
    private String timeZone;
    private Map<String, Boolean> notificationPreferences;

    // Getters & Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getPronouns() { return pronouns; }
    public void setPronouns(String pronouns) { this.pronouns = pronouns; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getOrcidId() { return orcidId; }
    public void setOrcidId(String orcidId) { this.orcidId = orcidId; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public SocialLinkDTO getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(SocialLinkDTO socialLinks) {
        this.socialLinks = socialLinks;
    }

    public List<EducationDTO> getEducation() { return education; }
    public void setEducation(List<EducationDTO> education) { this.education = education; }

    public List<WorkExperienceDTO> getWorkExperience() { return workExperience; }
    public void setWorkExperience(List<WorkExperienceDTO> workExperience) { this.workExperience = workExperience; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }

    public List<String> getAchievements() { return achievements; }
    public void setAchievements(List<String> achievements) { this.achievements = achievements; }

    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }

    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    public Map<String, Boolean> getNotificationPreferences() { return notificationPreferences; }
    public void setNotificationPreferences(Map<String, Boolean> notificationPreferences) { this.notificationPreferences = notificationPreferences; }
}