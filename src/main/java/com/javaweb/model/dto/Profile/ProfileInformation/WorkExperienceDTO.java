package com.javaweb.model.dto.Profile.ProfileInformation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkExperienceDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("company")
    private String company;

    @JsonProperty("position")
    private String position;

    @JsonProperty("location")
    private String location;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;

    @JsonProperty("current")
    private Boolean current;

    @JsonProperty("description")
    private String description;

    // Constructors
    public WorkExperienceDTO() {}

    public WorkExperienceDTO(Long id, String company, String position, String location,
                             String startDate, String endDate, Boolean current, String description) {
        this.id = id;
        this.company = company;
        this.position = position;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.current = current;
        this.description = description;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public Boolean getCurrent() { return current; }
    public void setCurrent(Boolean current) { this.current = current; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}