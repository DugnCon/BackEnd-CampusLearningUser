package com.javaweb.model.dto;
import lombok.Data;

@Data
public class NotificationSettingsDTO {
    private Boolean email; // Khớp với `notifications.email`
    private Boolean push;  // Khớp với `notifications.push`
    private Boolean inApp; // Bạn có trường 'NotificationInApp'
	public Boolean getEmail() {
		return email;
	}
	public void setEmail(Boolean email) {
		this.email = email;
	}
	public Boolean getPush() {
		return push;
	}
	public void setPush(Boolean push) {
		this.push = push;
	}
	public Boolean getInApp() {
		return inApp;
	}
	public void setInApp(Boolean inApp) {
		this.inApp = inApp;
	}

    // !! Trường 'courseUpdates' trong React không có trong DB
    // private Boolean courseUpdates;
    
}