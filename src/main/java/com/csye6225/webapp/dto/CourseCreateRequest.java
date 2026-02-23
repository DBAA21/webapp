package com.csye6225.webapp.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CourseCreateRequest {

    @NotBlank(message = "department_code is required")
    @Pattern(regexp = "^[A-Z]{2,6}$", message = "department_code must be 2-6 uppercase letters")
    @JsonProperty("department_code")
    private String departmentCode;

    @NotBlank(message = "number is required")
    @Size(min = 1, max = 6, message = "number must be between 1 and 6 characters")
    @JsonProperty("number")
    private String number;

    @NotBlank(message = "title is required")
    @Size(min = 1, max = 255, message = "title must be between 1 and 255 characters")
    @JsonProperty("title")
    private String title;

    @NotNull(message = "credit_hours is required")
    @Min(value = 1, message = "credit_hours must be at least 1")
    @Max(value = 8, message = "credit_hours must be at most 8")
    @JsonProperty("credit_hours")
    private Integer creditHours;

    @NotBlank(message = "classification is required")
    @Pattern(regexp = "^(core|elective)$", message = "classification must be 'core' or 'elective'")
    @JsonProperty("classification")
    private String classification;

    @Size(max = 2000, message = "description must be at most 2000 characters")
    @JsonProperty("description")
    private String description;

    @Size(max = 512, message = "prerequisites must be at most 512 characters")
    @JsonProperty("prerequisites")
    private String prerequisites;

    // Getters and Setters

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(Integer creditHours) {
        this.creditHours = creditHours;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }
}
