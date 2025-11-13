package com.javaweb.model.dto.Profile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailDTO {

    @JsonProperty("Email")
    private String email;

    @JsonProperty("IsVerified")
    private Boolean isVerified;

    @JsonProperty("IsPrimary")
    private Boolean isPrimary;

    public EmailDTO() {}

    public EmailDTO(String email, Boolean isVerified, Boolean isPrimary) {
        this.email = email;
        this.isVerified = isVerified;
        this.isPrimary = isPrimary;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
}