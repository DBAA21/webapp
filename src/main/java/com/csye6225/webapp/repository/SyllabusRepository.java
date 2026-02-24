package com.csye6225.webapp.repository;

import com.csye6225.webapp.entity.Syllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SyllabusRepository extends JpaRepository<Syllabus, UUID> {
    Optional<Syllabus> findByCourseId(String courseId);
    boolean existsByCourseId(String courseId);
    void deleteByCourseId(String courseId);
}
