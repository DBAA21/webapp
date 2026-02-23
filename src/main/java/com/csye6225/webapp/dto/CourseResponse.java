package com.csye6225.webapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public class CourseResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("department_code")
    private String departmentCode;

    @JsonProperty("number")
    private String number;

    @JsonProperty("title")
    private String title;

    @JsonProperty("credit_hours")
    private Integer creditHours;

    @JsonProperty("classification")
    private String classification;

    @JsonProperty("description")
    private String description;

    @JsonProperty("prerequisites")
    private String prerequisites;

    @JsonProperty("has_syllabus")
    private Boolean hasSyllabus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonProperty("date_created")
    private LocalDateTime dateCreated;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonProperty("date_updated")
    private LocalDateTime dateUpdated;

    // Constructors
    public CourseResponse() {}

    public CourseResponse(UUID id, String departmentCode, String number, String title,
                          Integer creditHours, String classification, String description,
                          String prerequisites, Boolean hasSyllabus,
                          LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        this.id = id;
        this.departmentCode = departmentCode;
        this.number = number;
        this.title = title;
        this.creditHours = creditHours;
        this.classification = classification;
        this.description = description;
        this.prerequisites = prerequisites;
        this.hasSyllabus = hasSyllabus;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getCreditHours() { return creditHours; }
    public void setCreditHours(Integer creditHours) { this.creditHours = creditHours; }
    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPrerequisites() { return prerequisites; }
    public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }
    public Boolean getHasSyllabus() { return hasSyllabus; }
    public void setHasSyllabus(Boolean hasSyllabus) { this.hasSyllabus = hasSyllabus; }
    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }
    public LocalDateTime getDateUpdated() { return dateUpdated; }
    public void setDateUpdated(LocalDateTime dateUpdated) { this.dateUpdated = dateUpdated; }
}
