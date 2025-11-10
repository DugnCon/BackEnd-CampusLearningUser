package com.javaweb.model.dto;
import lombok.Data;

@Data
public class CombinedSettingsResponseDTO {
    private ProfileInfoDTO profileInfo;
    private UserSettingsDTO settings;
	public ProfileInfoDTO getProfileInfo() {
		return profileInfo;
	}
	public void setProfileInfo(ProfileInfoDTO profileInfo) {
		this.profileInfo = profileInfo;
	}
	public UserSettingsDTO getSettings() {
		return settings;
	}
	public void setSettings(UserSettingsDTO settings) {
		this.settings = settings;
	}
    
}