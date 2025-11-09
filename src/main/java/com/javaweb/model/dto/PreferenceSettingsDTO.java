package com.javaweb.model.dto;
import lombok.Data;

@Data
public class PreferenceSettingsDTO {
    private String theme;
    private String language;
    private String timeZone;
}