package com.nuvixtech.courses.repository;

import com.nuvixtech.courses.model.Course;
import com.nuvixtech.courses.model.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCode(String code);

    List<Course> findByType(CourseType type);

    List<Course> findByNameContainingIgnoreCase(String name);

    boolean existsByCode(String code);
}
