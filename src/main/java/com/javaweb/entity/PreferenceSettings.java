package com.javaweb.entity;

import javax.persistence.Embeddable;

@Embeddable
public class PreferenceSettings {
    private String theme = "light"; // 'light' hoặc 'dark'
    private String language = "vi";

    // Getters and Setters...
}