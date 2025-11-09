package com.javaweb.model.dto;
import lombok.Data;

@Data
public class NotificationSettingsDTO {
    private Boolean email; // Khớp với `notifications.email`
    private Boolean push;  // Khớp với `notifications.push`
    private Boolean inApp; // Bạn có trường 'NotificationInApp'

    // !! Trường 'courseUpdates' trong React không có trong DB
    // private Boolean courseUpdates;
}