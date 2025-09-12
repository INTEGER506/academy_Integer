package com.ac.kr.academy.dto.course;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class CourseCreateRequestDTO {

    private Long id; // MyBatis가 생성한 ID를 설정하기 위해 추가
    private Long professorId;
    private Long subjectId;
    private Long semesterId;
    private int capacity;
    private String dayOfWeek;
    private String place;
    private String time;

    // Lombok이 제대로 작동하지 않을 경우를 대비해 setter를 명시적으로 추가
    public void setId(Long id) {
        this.id = id;
    }
//    @NotNull(message = "정원은 필수 입력값입니다.")
//    @Min(value = 4, message = "정원은 최소 4명입니다.")
//    @Max(value = 30, message = "정원은 최대 30명입니다.")
//    private Integer capacity;
//
//    @NotBlank(message = "강의실은 필수 입력값입니다.")
//    private String place;
//
//    @NotBlank(message = "요일은 필수 입력값입니다.")
//    private String dayOfWeek;
//
//    @NotBlank(message = "시간은 필수 입력값입니다.")
//    private String time;
//
//    @NotNull(message = "학기 ID는 필수 입력값입니다.")
//    private Long semesterId;
//
//    @NotNull(message = "교수 ID는 필수 입력값입니다.")
//    private Long professorId;
//
//    @NotNull(message = "과목 ID는 필수 입력값입니다.")
//    private Long subjectId;


}
