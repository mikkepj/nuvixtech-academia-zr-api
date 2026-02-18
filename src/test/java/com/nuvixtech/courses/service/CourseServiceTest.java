package com.nuvixtech.courses.service;

import com.nuvixtech.courses.dto.CourseRequest;
import com.nuvixtech.courses.dto.CourseResponse;
import com.nuvixtech.courses.exception.CourseNotFoundException;
import com.nuvixtech.courses.model.Course;
import com.nuvixtech.courses.model.CourseType;
import com.nuvixtech.courses.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void shouldReturnAllCourses() {
        given(courseRepository.findAll()).willReturn(List.of(buildCourse(1L), buildCourse(2L)));

        List<CourseResponse> result = courseService.findAll();

        assertThat(result).hasSize(2);
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
}
