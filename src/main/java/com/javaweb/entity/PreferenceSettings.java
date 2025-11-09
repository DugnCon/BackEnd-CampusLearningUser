package com.javaweb.entity;

import javax.persistence.Embeddable;

@Embeddable
public class PreferenceSettings {
    private String theme = "light";
    private String language = "vi";

}