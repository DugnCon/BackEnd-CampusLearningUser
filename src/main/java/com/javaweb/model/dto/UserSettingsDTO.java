package com.javaweb.model.dto;

import lombok.Data;

@Data
public class UserSettingsDTO {
    // Các DTO con lồng nhau, giống hệt React state
    private NotificationSettingsDTO notifications;
    private AccessibilitySettingsDTO accessibility;
    private PreferenceSettingsDTO preferences;

    // Các trường khác mà React có thể cần (ví dụ: `ProfileVisibility`)
    private String profileVisibility;

	public NotificationSettingsDTO getNotifications() {
		return notifications;
	}

	public void setNotifications(NotificationSettingsDTO notifications) {
		this.notifications = notifications;
	}

	public AccessibilitySettingsDTO getAccessibility() {
		return accessibility;
	}

	public void setAccessibility(AccessibilitySettingsDTO accessibility) {
		this.accessibility = accessibility;
	}

	public PreferenceSettingsDTO getPreferences() {
		return preferences;
	}

	public void setPreferences(PreferenceSettingsDTO preferences) {
		this.preferences = preferences;
	}

	public String getProfileVisibility() {
		return profileVisibility;
	}

	public void setProfileVisibility(String profileVisibility) {
		this.profileVisibility = profileVisibility;
	}
    
}