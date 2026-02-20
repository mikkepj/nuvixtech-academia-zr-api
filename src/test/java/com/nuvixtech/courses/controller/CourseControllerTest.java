package com.nuvixtech.courses.controller;

import tools.jackson.databind.ObjectMapper;
import com.nuvixtech.courses.dto.CourseRequest;
import com.nuvixtech.courses.dto.CourseResponse;
import com.nuvixtech.courses.dto.PagedResponse;
import com.nuvixtech.courses.exception.CourseNotFoundException;
import com.nuvixtech.courses.model.CourseType;
import com.nuvixtech.courses.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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

    private PagedResponse<CourseResponse> buildPage(CourseResponse... items) {
        return PagedResponse.<CourseResponse>builder()
                .content(List.of(items))
                .page(0)
                .size(10)
                .totalElements(items.length)
                .totalPages(1)
                .last(true)
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
    void shouldReturn200WithAllCourses() throws Exception {
        given(courseService.findAll(isNull(), isNull(), any(Pageable.class)))
                .willReturn(buildPage(buildResponse(1L), buildResponse(2L)));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].code").value("JAVA-101"))
                .andExpect(jsonPath("$.totalElements").value(2));
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

    // ── Sprint 2 — Validation Tests ────────────────────────────────

    @Test
    void shouldReturn400WhenCreatingWithEmptyBody() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors.code").value("El código es obligatorio"))
                .andExpect(jsonPath("$.errors.name").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.errors.duration").value("La duración es obligatoria"))
                .andExpect(jsonPath("$.errors.type").value("El tipo es obligatorio"))
                .andExpect(jsonPath("$.errors.price").value("El precio es obligatorio"));
    }

    @Test
    void shouldReturn400WhenCreatingWithBlankCode() throws Exception {
        CourseRequest request = buildRequest();
        request.setCode("   ");

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").value("El código es obligatorio"));
    }

    @Test
    void shouldReturn400WhenCodeExceeds20Characters() throws Exception {
        CourseRequest request = buildRequest();
        request.setCode("A".repeat(21));

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").value("El código no puede exceder 20 caracteres"));
    }

    @Test
    void shouldReturn400WhenDurationIsNegative() throws Exception {
        CourseRequest request = buildRequest();
        request.setDuration(-5);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.duration").value("La duración debe ser un número positivo"));
    }

    @Test
    void shouldReturn400WhenPriceIsZero() throws Exception {
        CourseRequest request = buildRequest();
        request.setPrice(BigDecimal.ZERO);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").value("El precio debe ser un número positivo"));
    }

    @Test
    void shouldReturn400WhenUpdatingWithInvalidData() throws Exception {
        CourseRequest request = new CourseRequest();

        mockMvc.perform(put("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    void shouldNotCallServiceWhenValidationFails() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        then(courseService).shouldHaveNoInteractions();
    }

    // ── Sprint 3 — Filtros y Paginación Tests ──────────────────────

    @Test
    void shouldReturn200WithPaginationMetadata() throws Exception {
        given(courseService.findAll(isNull(), isNull(), any(Pageable.class)))
                .willReturn(buildPage(buildResponse(1L)));

        mockMvc.perform(get("/api/courses").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void shouldFilterByType() throws Exception {
        given(courseService.findAll(eq(CourseType.ONLINE), isNull(), any(Pageable.class)))
                .willReturn(buildPage(buildResponse(1L)));

        mockMvc.perform(get("/api/courses").param("type", "ONLINE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void shouldFilterByName() throws Exception {
        given(courseService.findAll(isNull(), eq("java"), any(Pageable.class)))
                .willReturn(buildPage(buildResponse(1L)));

        mockMvc.perform(get("/api/courses").param("name", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void shouldFilterByTypeAndName() throws Exception {
        given(courseService.findAll(eq(CourseType.PRESENCIAL), eq("java"), any(Pageable.class)))
                .willReturn(buildPage(buildResponse(1L)));

        mockMvc.perform(get("/api/courses")
                        .param("type", "PRESENCIAL")
                        .param("name", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("PRESENCIAL"));
    }

    @Test
    void shouldReturnEmptyPageWhenNoMatches() throws Exception {
        PagedResponse<CourseResponse> emptyPage = PagedResponse.<CourseResponse>builder()
                .content(List.of())
                .page(0)
                .size(10)
                .totalElements(0)
                .totalPages(0)
                .last(true)
                .build();

        given(courseService.findAll(eq(CourseType.ONLINE), isNull(), any(Pageable.class)))
                .willReturn(emptyPage);

        mockMvc.perform(get("/api/courses").param("type", "ONLINE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
