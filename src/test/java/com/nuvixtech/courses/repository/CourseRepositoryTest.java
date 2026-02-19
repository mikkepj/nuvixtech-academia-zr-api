package com.nuvixtech.courses.repository;

import com.nuvixtech.courses.model.Course;
import com.nuvixtech.courses.model.CourseType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@Commit
@Sql("/test-courses.sql")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void shouldLoad10CoursesFromSqlScript() {
        assertThat(courseRepository.count()).isEqualTo(10);
    }

    @Test
    void shouldFindAllCourses() {
        List<Course> courses = courseRepository.findAll();
        assertThat(courses).hasSize(10);
    }

    @Test
    void shouldFindCourseByCode() {
        Optional<Course> course = courseRepository.findByCode("JAVA-101");
        assertThat(course).isPresent();
        assertThat(course.get().getName()).isEqualTo("Java Fundamentals");
    }

    @Test
    void shouldReturnEmptyWhenCodeNotFound() {
        Optional<Course> course = courseRepository.findByCode("NONEXISTENT");
        assertThat(course).isEmpty();
    }

    @Test
    void shouldFindCoursesByType() {
        List<Course> online = courseRepository.findByType(CourseType.ONLINE);
        assertThat(online).isNotEmpty();
        assertThat(online).allMatch(c -> c.getType() == CourseType.ONLINE);
    }

    @Test
    void shouldFindCoursesByNameContainingIgnoreCase() {
        List<Course> results = courseRepository.findByNameContainingIgnoreCase("java");
        assertThat(results).hasSize(2);
    }

    @Test
    void shouldSaveNewCourse() {
        Course course = Course.builder()
                .code("TEST-999")
                .name("Test Course")
                .description("Test description")
                .duration(10)
                .type(CourseType.ONLINE)
                .price(new BigDecimal("99.99"))
                .build();
        Course saved = courseRepository.save(course);
        assertThat(saved.getId()).isNotNull();
        assertThat(courseRepository.count()).isEqualTo(11);
    }

    @Test
    void shouldFindCourseById() {
        Optional<Course> course = courseRepository.findByCode("SPRING-101");
        assertThat(course).isPresent();

        Optional<Course> found = courseRepository.findById(course.get().getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("SPRING-101");
    }

    @Test
    void shouldUpdateCourse() {
        Optional<Course> course = courseRepository.findByCode("JAVA-101");
        assertThat(course).isPresent();

        course.get().setName("Java Fundamentals Updated");
        courseRepository.save(course.get());

        Optional<Course> updated = courseRepository.findByCode("JAVA-101");
        assertThat(updated.get().getName()).isEqualTo("Java Fundamentals Updated");
    }

    @Test
    void shouldDeleteCourse() {
        Optional<Course> course = courseRepository.findByCode("JAVA-101");
        assertThat(course).isPresent();

        courseRepository.deleteById(course.get().getId());
        assertThat(courseRepository.count()).isEqualTo(9);
    }

    @Test
    void shouldReturnTrueForExistingCode() {
        assertThat(courseRepository.existsByCode("PYTH-101")).isTrue();
        assertThat(courseRepository.existsByCode("INVALID")).isFalse();
    }
}