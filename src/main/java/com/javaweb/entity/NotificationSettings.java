package com.javaweb.entity;

import javax.persistence.Embeddable;

@Embeddable
public class NotificationSettings {
    private boolean email = true;
    private boolean push = true;
    private boolean courseUpdates = true;

    // Getters and Setters...
}