package com.ac.kr.academy.dto.course;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class CourseRequestDTO {

    @Min(value = 4)
    @Max(value = 30 )
    private Integer capacity;

    @NotBlank
    private String place;

    @NotBlank
    private String dayOfWeek;

    @NotNull
    private Long semesterId;

    @NotNull
    private Long subjectId;

    @NotBlank
    private String title;

    @Size(max = 50)
    private String department;

    private Integer year;

    @Size(max = 200)
    private String description;

}
