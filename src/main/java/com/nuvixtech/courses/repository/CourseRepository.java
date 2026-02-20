package com.nuvixtech.courses.repository;

import com.nuvixtech.courses.model.Course;
import com.nuvixtech.courses.model.CourseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>,
        JpaSpecificationExecutor<Course> {

    Optional<Course> findByCode(String code);

    Page<Course> findByType(CourseType type, Pageable pageable);

    Page<Course> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Course> findByTypeAndNameContainingIgnoreCase(CourseType type, String name, Pageable pageable);

    boolean existsByCode(String code);
}
