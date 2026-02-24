package com.csye6225.webapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "syllabi")
public class Syllabus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonProperty("id")
    private UUID id;

    @NotBlank
    @Column(name = "course_id", nullable = false)
    @JsonProperty("course_id")
    private String courseId;

    @NotBlank
    @Column(name = "file_name", nullable = false)
    @JsonProperty("file_name")
    private String fileName;

    @NotBlank
    @Column(name = "s3_bucket_name", nullable = false)
    @JsonProperty("s3_bucket_name")
    private String s3BucketName;

    @NotBlank
    @Column(name = "s3_object_key", nullable = false, length = 1024)
    @JsonProperty("s3_object_key")
    private String s3ObjectKey;

    @NotBlank
    @Column(name = "content_type", nullable = false)
    @JsonProperty("content_type")
    private String contentType;

    @NotNull
    @Column(name = "file_size", nullable = false)
    @JsonProperty("file_size")
    private Long fileSize;

    @NotBlank
    @Column(name = "url", nullable = false, length = 1024)
    @JsonProperty("url")
    private String url;

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

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getS3BucketName() {
        return s3BucketName;
    }

    public void setS3BucketName(String s3BucketName) {
        this.s3BucketName = s3BucketName;
    }

    public String getS3ObjectKey() {
        return s3ObjectKey;
    }

    public void setS3ObjectKey(String s3ObjectKey) {
        this.s3ObjectKey = s3ObjectKey;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
