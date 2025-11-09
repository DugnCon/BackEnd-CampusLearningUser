package com.javaweb.model.dto;

import com.javaweb.model.dto.UserSuggestions.UserSuggestionDTO;
import lombok.Data;
import java.time.LocalDateTime;

public class StoryDTO {
    private Long storyID;
    private Long userID;
    private String userFullName;
    private String userImage;
    private String mediaType;
    private String mediaUrl;
    private String textContent;
    private String backgroundColor;
    private String fontStyle;
    private Integer duration;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Long viewCount;
    private Long likeCount;
    private Boolean isViewed;
    private Boolean isLiked;
    private UserSuggestionDTO user;

    // Default constructor
    public StoryDTO() {}

    public UserSuggestionDTO getUser() {
        return user;
    }

    public void setUser(UserSuggestionDTO user) {
        this.user = user;
    }

    // Getters
    public Long getStoryID() {
        return storyID;
    }

    public Long getUserID() {
        return userID;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getTextContent() {
        return textContent;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public Integer getDuration() {
        return duration;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public Boolean getIsViewed() {
        return isViewed;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    // Setters
    public void setStoryID(Long storyID) {
        this.storyID = storyID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public void setIsViewed(Boolean isViewed) {
        this.isViewed = isViewed;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    // Optional: toString method for debugging
    @Override
    public String toString() {
        return "StoryDTO{" +
                "storyID=" + storyID +
                ", userID=" + userID +
                ", userFullName='" + userFullName + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
