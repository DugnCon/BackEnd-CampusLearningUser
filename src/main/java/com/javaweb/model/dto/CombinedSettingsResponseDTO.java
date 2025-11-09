package com.javaweb.model.dto;
import lombok.Data;

@Data
public class CombinedSettingsResponseDTO {
    private ProfileInfoDTO profileInfo;
    private UserSettingsDTO settings;
}