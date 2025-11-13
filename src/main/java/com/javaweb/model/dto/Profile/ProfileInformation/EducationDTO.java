package com.javaweb.model.dto.Profile.ProfileInformation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EducationDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("school")
    private String school;

    @JsonProperty("degree")
    private String degree;

    @JsonProperty("field")
    private String field;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;

    @JsonProperty("current")
    private Boolean current;

    @JsonProperty("description")
    private String description;

    // Constructors
    public EducationDTO() {}

    public EducationDTO(Long id, String school, String degree, String field,
                        String startDate, String endDate, Boolean current, String description) {
        this.id = id;
        this.school = school;
        this.degree = degree;
        this.field = field;
        this.startDate = startDate;
        this.endDate = endDate;
        this.current = current;
        this.description = description;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public Boolean getCurrent() { return current; }
    public void setCurrent(Boolean current) { this.current = current; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}