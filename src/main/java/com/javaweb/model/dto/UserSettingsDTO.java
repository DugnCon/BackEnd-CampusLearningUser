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
}