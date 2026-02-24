package com.csye6225.webapp.controller;

import com.csye6225.webapp.dto.ErrorResponse;
import com.csye6225.webapp.dto.SyllabusResponse;
import com.csye6225.webapp.service.SyllabusService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/v1/courses/{course_id}/syllabus")
public class SyllabusController {

    @Autowired
    private SyllabusService syllabusService;

    /**
     * POST /v1/courses/{course_id}/syllabus — Upload syllabus file
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadSyllabus(
            @PathVariable("course_id") String courseId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {
        try {
            UUID id = UUID.fromString(courseId);

            // Validate file
            if (file == null || file.isEmpty()) {
                ErrorResponse error = new ErrorResponse(
                    "Bad Request",
                    "File must not be null or empty",
                    request.getRequestURI()
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            SyllabusResponse response = syllabusService.uploadSyllabus(id, file);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .header("Location", "/v1/courses/" + courseId + "/syllabus")
                    .body(response);

        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                ErrorResponse error = new ErrorResponse("Conflict", e.getMessage(), request.getRequestURI());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            // Invalid UUID format
            ErrorResponse error = new ErrorResponse("Not Found", "Course not found", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Course not found")) {
                ErrorResponse error = new ErrorResponse("Not Found", "Course not found", request.getRequestURI());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            ErrorResponse error = new ErrorResponse("Internal Server Error", "Error uploading syllabus", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Internal Server Error", "Error uploading syllabus", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /v1/courses/{course_id}/syllabus — Get syllabus metadata
     */
    @GetMapping
    public ResponseEntity<?> getSyllabus(
            @PathVariable("course_id") String courseId,
            HttpServletRequest request) {
        try {
            UUID id = UUID.fromString(courseId);
            SyllabusResponse response = syllabusService.getSyllabus(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse("Not Found", "Course not found", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Course not found")) {
                ErrorResponse error = new ErrorResponse("Not Found", "Course not found", request.getRequestURI());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            if (e.getMessage() != null && e.getMessage().contains("No syllabus found")) {
                ErrorResponse error = new ErrorResponse("Not Found", e.getMessage(), request.getRequestURI());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            ErrorResponse error = new ErrorResponse("Internal Server Error", "Error retrieving syllabus", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * DELETE /v1/courses/{course_id}/syllabus — Delete syllabus
     */
    @DeleteMapping
    public ResponseEntity<?> deleteSyllabus(
            @PathVariable("course_id") String courseId,
            HttpServletRequest request) {
        try {
            UUID id = UUID.fromString(courseId);
            syllabusService.deleteSyllabus(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse("Not Found", "Course not found", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Course not found")) {
                ErrorResponse error = new ErrorResponse("Not Found", "Course not found", request.getRequestURI());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            if (e.getMessage() != null && e.getMessage().contains("No syllabus found")) {
                ErrorResponse error = new ErrorResponse("Not Found", e.getMessage(), request.getRequestURI());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            ErrorResponse error = new ErrorResponse("Internal Server Error", "Error deleting syllabus", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
