package com.ac.kr.academy.dto.course;




import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CourseCreateRequestDTO {

    @NotBlank(message = "강좌명은 필수 입력값입니다.")
    private String subjectName;

    @NotNull(message = "학점은 필수 입력값입니다.")
    private Integer credit;

    private  String description;

    @NotNull(message = "학과는 필수 입력값입니다.")
    private Long deptId;

    @Min(value = 4, message = "정원은 최소 4명입니다.")
    @Max(value = 30, message = "정원은 최대 30명입니다.")
    private Integer capacity;

    @NotBlank(message = "강의실은 필수 입력값입니다.")
    private String place;

    @NotBlank(message = "요일은 필수 입력값입니다.")
    private String dayOfWeek;

    private String time;

    @NotNull(message = "학기 ID는 필수 입력값입니다.")
    private Long semesterId;

    @NotNull(message = "교수 ID는 필수 입력값입니다.")
    private Long professorId;
}
