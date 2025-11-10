package com.javaweb.model.dto;
import lombok.Data;

@Data
public class PreferenceSettingsDTO {
    private String theme;
    private String language;
    private String timeZone;
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
    
}