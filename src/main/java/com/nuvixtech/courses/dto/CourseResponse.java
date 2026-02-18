package com.nuvixtech.courses.dto;

import com.nuvixtech.courses.model.CourseType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CourseResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer duration;
    private CourseType type;
    private BigDecimal price;
}
