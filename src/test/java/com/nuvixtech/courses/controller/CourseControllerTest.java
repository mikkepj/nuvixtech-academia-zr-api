package com.nuvixtech.courses.controller;

import tools.jackson.databind.ObjectMapper;
import com.nuvixtech.courses.dto.CourseRequest;
import com.nuvixtech.courses.dto.CourseResponse;
import com.nuvixtech.courses.exception.CourseNotFoundException;
import com.nuvixtech.courses.model.CourseType;
import com.nuvixtech.courses.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseService;

    private CourseResponse buildResponse(Long id) {
        return CourseResponse.builder()
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
    void shouldReturn200WithAllCourses() throws Exception {
        given(courseService.findAll()).willReturn(List.of(buildResponse(1L), buildResponse(2L)));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("JAVA-101"));
    }

    @Test
    void shouldReturn200WithCourseById() throws Exception {
        given(courseService.findById(1L)).willReturn(buildResponse(1L));

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("JAVA-101"))
                .andExpect(jsonPath("$.type").value("PRESENCIAL"));
    }

    @Test
    void shouldReturn404WhenCourseNotFound() throws Exception {
        given(courseService.findById(99L)).willThrow(new CourseNotFoundException(99L));

        mockMvc.perform(get("/api/courses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn201WhenCourseCreated() throws Exception {
        given(courseService.create(any(CourseRequest.class))).willReturn(buildResponse(1L));

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("JAVA-101"));
    }

    @Test
    void shouldReturn200WhenCourseUpdated() throws Exception {
        given(courseService.update(eq(1L), any(CourseRequest.class))).willReturn(buildResponse(1L));

        mockMvc.perform(put("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("JAVA-101"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonexistentCourse() throws Exception {
        given(courseService.update(eq(99L), any(CourseRequest.class)))
                .willThrow(new CourseNotFoundException(99L));

        mockMvc.perform(put("/api/courses/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn204WhenCourseDeleted() throws Exception {
        willDoNothing().given(courseService).delete(1L);

        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonexistentCourse() throws Exception {
        willThrow(new CourseNotFoundException(99L)).given(courseService).delete(99L);

        mockMvc.perform(delete("/api/courses/99"))
                .andExpect(status().isNotFound());
    }
}
