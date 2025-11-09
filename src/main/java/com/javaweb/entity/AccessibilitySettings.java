package com.javaweb.entity;

import javax.persistence.Embeddable;

@Embeddable
public class AccessibilitySettings {
    private boolean screenReader = false;
    private boolean imageDescriptions = false;
    private boolean focusIndicator = true;
    private int keyboardDelay = 0;
    private boolean reducedMotion = false;
    private boolean disableEffects = false;
    private int animationSpeed = 100;
    private boolean highContrast = false;
    private int letterSpacing = 0;
    private int lineHeight = 150;
    private boolean alwaysShowCaptions = false;
    private boolean preventAutoplay = false;

    // Getters and Setters...
}