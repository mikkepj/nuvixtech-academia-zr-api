package com.nuvixtech.courses.service;

import com.nuvixtech.courses.dto.CourseRequest;
import com.nuvixtech.courses.dto.CourseResponse;
import com.nuvixtech.courses.dto.PagedResponse;
import com.nuvixtech.courses.exception.CourseNotFoundException;
import com.nuvixtech.courses.model.Course;
import com.nuvixtech.courses.model.CourseType;
import com.nuvixtech.courses.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public PagedResponse<CourseResponse> findAll(CourseType type, String name, Pageable pageable) {
        Page<Course> page;

        if (type != null && name != null && !name.isBlank()) {
            page = courseRepository.findByTypeAndNameContainingIgnoreCase(type, name, pageable);
        } else if (type != null) {
            page = courseRepository.findByType(type, pageable);
        } else if (name != null && !name.isBlank()) {
            page = courseRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            page = courseRepository.findAll(pageable);
        }

        return PagedResponse.from(page.map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        return courseRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    public CourseResponse create(CourseRequest request) {
        Course course = Course.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .duration(request.getDuration())
                .type(request.getType())
                .price(request.getPrice())
                .build();
        return toResponse(courseRepository.save(course));
    }

    public CourseResponse update(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        course.setCode(request.getCode());
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setDuration(request.getDuration());
        course.setType(request.getType());
        course.setPrice(request.getPrice());
        return toResponse(courseRepository.save(course));
    }

    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new CourseNotFoundException(id);
        }
        courseRepository.deleteById(id);
    }

    private CourseResponse toResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .description(course.getDescription())
                .duration(course.getDuration())
                .type(course.getType())
                .price(course.getPrice())
                .build();
    }
}
