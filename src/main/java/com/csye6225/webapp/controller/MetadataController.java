package com.csye6225.webapp.controller;

import com.csye6225.webapp.dto.MetadataResponse;
import com.csye6225.webapp.exception.MetadataUnavailableException;
import com.csye6225.webapp.service.AwsMetadataService;
import com.csye6225.webapp.service.CloudPlatformDetector;
import com.csye6225.webapp.service.GcpMetadataService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetadataController {

    private final CloudPlatformDetector cloudPlatformDetector;
    private final AwsMetadataService awsMetadataService;
    private final GcpMetadataService gcpMetadataService;

    public MetadataController(
            CloudPlatformDetector cloudPlatformDetector,
            AwsMetadataService awsMetadataService,
            GcpMetadataService gcpMetadataService) {
        this.cloudPlatformDetector = cloudPlatformDetector;
        this.awsMetadataService = awsMetadataService;
        this.gcpMetadataService = gcpMetadataService;
    }

    @GetMapping("/v1/metadata")
    public ResponseEntity<?> getMetadata(
            @RequestBody(required = false) String body,
            HttpServletRequest request) {
        if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
            return badRequest();
        }

        if (body != null && !body.isEmpty()) {
            return badRequest();
        }

        String platform = cloudPlatformDetector.detect();
        if (platform == null) {
            return serviceUnavailable();
        }

        try {
            MetadataResponse response = "aws".equals(platform)
                    ? awsMetadataService.getMetadata()
                    : gcpMetadataService.getMetadata();
            return ResponseEntity.ok().headers(noCacheHeaders()).body(response);
        } catch (MetadataUnavailableException ex) {
            return serviceUnavailable();
        }
    }

    @RequestMapping(
            value = "/v1/metadata",
            method = {
                    RequestMethod.POST,
                    RequestMethod.PUT,
                    RequestMethod.DELETE,
                    RequestMethod.PATCH,
                    RequestMethod.HEAD,
                    RequestMethod.OPTIONS
            })
    public ResponseEntity<Map<String, String>> metadataNotAllowed() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .headers(noCacheHeaders())
                .build();
    }

    private ResponseEntity<Map<String, String>> badRequest() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(noCacheHeaders())
                .body(Map.of("error", "Bad Request"));
    }

    private ResponseEntity<Map<String, String>> serviceUnavailable() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .headers(noCacheHeaders())
                .body(Map.of("error", "Metadata service unavailable - not running on supported cloud platform"));
    }

    private HttpHeaders noCacheHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("X-Content-Type-Options", "nosniff");
        return headers;
    }
}
