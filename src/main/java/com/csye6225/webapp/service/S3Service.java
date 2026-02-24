package com.csye6225.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;

    public S3Service(@Autowired(required = false) S3Client s3Client,
                     @Value("${aws.s3.bucket-name:}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }

    /**
     * Upload file to S3.
     * @param objectKey the S3 object key (e.g., "courseId/uuid/filename")
     * @param content the file bytes
     * @param contentType MIME type
     * @return the S3 URL
     */
    public String uploadFile(String objectKey, byte[] content, String contentType) {
        if (s3Client == null) {
            throw new IllegalStateException("S3 is not configured");
        }
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();
        s3Client.putObject(putRequest, RequestBody.fromBytes(content));
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, objectKey);
    }

    /**
     * Delete file from S3.
     */
    public void deleteFile(String objectKey) {
        if (s3Client == null) {
            throw new IllegalStateException("S3 is not configured");
        }
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        s3Client.deleteObject(deleteRequest);
    }
}
