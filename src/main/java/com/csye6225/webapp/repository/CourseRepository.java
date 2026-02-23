package com.csye6225.webapp.repository;

import com.csye6225.webapp.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    boolean existsByDepartmentCodeAndNumber(String departmentCode, String number);
    List<Course> findAllByOrderByDepartmentCodeAscNumberAsc();
}
