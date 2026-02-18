package com.nuvixtech.courses.dto;

import com.nuvixtech.courses.model.CourseType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CourseRequest {
    private String code;
    private String name;
    private String description;
    private Integer duration;
    private CourseType type;
    private BigDecimal price;
}
