package com.javaweb.model.dto;
import lombok.Data;

@Data
public class AccessibilitySettingsDTO {
    private Boolean screenReader = false;
    private Boolean imageDescriptions = false;
    private Boolean focusIndicator = true;
    private Integer keyboardDelay = 0;
    private Boolean reducedMotion = false;
    private Boolean disableEffects = false;
    private Integer animationSpeed = 100;
    private Boolean highContrast = false;
    private Integer letterSpacing = 0;
    private Integer lineHeight = 150;
    private Boolean alwaysShowCaptions = false;
    private Boolean preventAutoplay = false;
}