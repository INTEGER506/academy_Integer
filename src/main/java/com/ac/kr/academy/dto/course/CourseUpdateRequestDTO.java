package com.ac.kr.academy.dto.course;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseUpdateRequestDTO {

    @Min(value = 4, message = "정원은 최소 4명입니다.")
    @Max(value = 30, message = "정원은 최대 30명입니다.")
    private Integer capacity;

    @NotBlank(message = "강의실은 필수 입력값입니다.")
    private String place;

    @NotBlank(message = "요일은 필수 입력값입니다.")
    private String dayOfWeek;

    @NotBlank(message = "강의 상태는 필수 입력값입니다.")
    private String status;

    private String time;
}
