package com.csye6225.webapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "courses", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"department_code", "number"})
})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonProperty("id")
    private UUID id;

    @NotBlank
    @Column(name = "department_code", nullable = false)
    @JsonProperty("department_code")
    private String departmentCode;

    @NotBlank
    @Column(name = "number", nullable = false)
    @JsonProperty("number")
    private String number;

    @NotBlank
    @Column(name = "title", nullable = false)
    @JsonProperty("title")
    private String title;

    @NotNull
    @Column(name = "credit_hours", nullable = false)
    @JsonProperty("credit_hours")
    private Integer creditHours;

    @NotBlank
    @Column(name = "classification", nullable = false)
    @JsonProperty("classification")
    private String classification;

    @Column(name = "description", length = 2000)
    @JsonProperty("description")
    private String description;

    @Column(name = "prerequisites", length = 512)
    @JsonProperty("prerequisites")
    private String prerequisites;

    @Column(name = "has_syllabus", nullable = false)
    @JsonProperty("has_syllabus")
    private Boolean hasSyllabus = false;

    @Column(name = "date_created", nullable = false, updatable = false)
    @JsonProperty("date_created")
    private LocalDateTime dateCreated;

    @Column(name = "date_updated", nullable = false)
    @JsonProperty("date_updated")
    private LocalDateTime dateUpdated;

    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDateTime.now();
        dateUpdated = LocalDateTime.now();
        if (hasSyllabus == null) {
            hasSyllabus = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dateUpdated = LocalDateTime.now();
    }

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public Boolean getHasSyllabus() {
        return hasSyllabus;
    }

    public void setHasSyllabus(Boolean hasSyllabus) {
        this.hasSyllabus = hasSyllabus;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
