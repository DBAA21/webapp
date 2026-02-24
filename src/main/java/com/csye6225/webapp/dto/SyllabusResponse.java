package com.csye6225.webapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public class SyllabusResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("course_id")
    private String courseId;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("s3_bucket_name")
    private String s3BucketName;

    @JsonProperty("s3_object_key")
    private String s3ObjectKey;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("file_size")
    private Long fileSize;

    @JsonProperty("url")
    private String url;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonProperty("date_created")
    private LocalDateTime dateCreated;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonProperty("date_updated")
    private LocalDateTime dateUpdated;

    // Constructors
    public SyllabusResponse() {}

    public SyllabusResponse(UUID id, String courseId, String fileName, String s3BucketName,
                            String s3ObjectKey, String contentType, Long fileSize, String url,
                            LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        this.id = id;
        this.courseId = courseId;
        this.fileName = fileName;
        this.s3BucketName = s3BucketName;
        this.s3ObjectKey = s3ObjectKey;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.url = url;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getS3BucketName() { return s3BucketName; }
    public void setS3BucketName(String s3BucketName) { this.s3BucketName = s3BucketName; }
    public String getS3ObjectKey() { return s3ObjectKey; }
    public void setS3ObjectKey(String s3ObjectKey) { this.s3ObjectKey = s3ObjectKey; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }
    public LocalDateTime getDateUpdated() { return dateUpdated; }
    public void setDateUpdated(LocalDateTime dateUpdated) { this.dateUpdated = dateUpdated; }
}
