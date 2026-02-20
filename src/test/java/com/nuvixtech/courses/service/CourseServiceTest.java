package com.nuvixtech.courses.service;

import com.nuvixtech.courses.dto.CourseRequest;
import com.nuvixtech.courses.dto.CourseResponse;
import com.nuvixtech.courses.dto.PagedResponse;
import com.nuvixtech.courses.exception.CourseNotFoundException;
import com.nuvixtech.courses.model.Course;
import com.nuvixtech.courses.model.CourseType;
import com.nuvixtech.courses.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private final Pageable pageable = PageRequest.of(0, 10);

    private Course buildCourse(Long id) {
        return Course.builder()
                .id(id)
                .code("JAVA-101")
                .name("Java Fundamentals")
                .description("Intro to Java")
                .duration(40)
                .type(CourseType.PRESENCIAL)
                .price(new BigDecimal("299.99"))
                .build();
    }

    private CourseRequest buildRequest() {
        CourseRequest request = new CourseRequest();
        request.setCode("JAVA-101");
        request.setName("Java Fundamentals");
        request.setDescription("Intro to Java");
        request.setDuration(40);
        request.setType(CourseType.PRESENCIAL);
        request.setPrice(new BigDecimal("299.99"));
        return request;
    }

    // ── Sprint 1 — CRUD Tests ──────────────────────────────────────

    @Test
    void shouldReturnAllCourses() {
        Page<Course> coursePage = new PageImpl<>(List.of(buildCourse(1L), buildCourse(2L)));
        given(courseRepository.findAll(pageable)).willReturn(coursePage);

        PagedResponse<CourseResponse> result = courseService.findAll(null, null, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldFindCourseById() {
        given(courseRepository.findById(1L)).willReturn(Optional.of(buildCourse(1L)));

        CourseResponse result = courseService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("JAVA-101");
        assertThat(result.getType()).isEqualTo(CourseType.PRESENCIAL);
    }

    @Test
    void shouldThrowWhenCourseNotFoundById() {
        given(courseRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.findById(99L))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldCreateCourse() {
        Course saved = buildCourse(1L);
        given(courseRepository.save(any(Course.class))).willReturn(saved);

        CourseResponse result = courseService.create(buildRequest());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("JAVA-101");
        then(courseRepository).should().save(any(Course.class));
    }

    @Test
    void shouldUpdateCourse() {
        Course existing = buildCourse(1L);
        given(courseRepository.findById(1L)).willReturn(Optional.of(existing));
        given(courseRepository.save(existing)).willReturn(existing);

        CourseRequest request = buildRequest();
        request.setName("Updated Java");

        CourseResponse result = courseService.update(1L, request);

        assertThat(result).isNotNull();
        then(courseRepository).should().save(existing);
    }

    @Test
    void shouldThrowOnUpdateWhenCourseNotFound() {
        given(courseRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.update(99L, buildRequest()))
                .isInstanceOf(CourseNotFoundException.class);
    }

    @Test
    void shouldDeleteCourse() {
        given(courseRepository.existsById(1L)).willReturn(true);
        willDoNothing().given(courseRepository).deleteById(1L);

        courseService.delete(1L);

        then(courseRepository).should().deleteById(1L);
    }

    @Test
    void shouldThrowOnDeleteWhenCourseNotFound() {
        given(courseRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> courseService.delete(99L))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── Sprint 3 — Filtros y Paginación Tests ──────────────────────

    @Test
    void shouldFilterByType() {
        Page<Course> coursePage = new PageImpl<>(List.of(buildCourse(1L)));
        given(courseRepository.findByType(CourseType.PRESENCIAL, pageable)).willReturn(coursePage);

        PagedResponse<CourseResponse> result = courseService.findAll(CourseType.PRESENCIAL, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getType()).isEqualTo(CourseType.PRESENCIAL);
        then(courseRepository).should().findByType(CourseType.PRESENCIAL, pageable);
    }

    @Test
    void shouldFilterByName() {
        Page<Course> coursePage = new PageImpl<>(List.of(buildCourse(1L)));
        given(courseRepository.findByNameContainingIgnoreCase("java", pageable)).willReturn(coursePage);

        PagedResponse<CourseResponse> result = courseService.findAll(null, "java", pageable);

        assertThat(result.getContent()).hasSize(1);
        then(courseRepository).should().findByNameContainingIgnoreCase("java", pageable);
    }

    @Test
    void shouldFilterByTypeAndName() {
        Page<Course> coursePage = new PageImpl<>(List.of(buildCourse(1L)));
        given(courseRepository.findByTypeAndNameContainingIgnoreCase(CourseType.PRESENCIAL, "java", pageable))
                .willReturn(coursePage);

        PagedResponse<CourseResponse> result = courseService.findAll(CourseType.PRESENCIAL, "java", pageable);

        assertThat(result.getContent()).hasSize(1);
        then(courseRepository).should()
                .findByTypeAndNameContainingIgnoreCase(CourseType.PRESENCIAL, "java", pageable);
    }

    @Test
    void shouldIgnoreBlankNameFilter() {
        Page<Course> coursePage = new PageImpl<>(List.of(buildCourse(1L)));
        given(courseRepository.findAll(pageable)).willReturn(coursePage);

        PagedResponse<CourseResponse> result = courseService.findAll(null, "   ", pageable);

        assertThat(result.getContent()).hasSize(1);
        then(courseRepository).should().findAll(pageable);
    }

    @Test
    void shouldReturnPaginationMetadata() {
        List<Course> courses = List.of(buildCourse(1L), buildCourse(2L), buildCourse(3L));
        Page<Course> coursePage = new PageImpl<>(courses, pageable, 25);
        given(courseRepository.findAll(pageable)).willReturn(coursePage);

        PagedResponse<CourseResponse> result = courseService.findAll(null, null, pageable);

        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(25);
        assertThat(result.getTotalPages()).isEqualTo(3);
    }
}
