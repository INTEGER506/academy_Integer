package com.ac.kr.academy.dto.course;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
public class CourseUpdateRequestDTO {

    private Long id;

    @Min(value = 4, message = "정원은 최소 4명입니다.")
    @Max(value = 30, message = "정원은 최대 30명입니다.")
    private Integer capacity;

    private String status; // OPEN / CLOSED 등

    // DB 저장용 문자열 필드
    private String dayOfWeek;
    private String place;
    private String time;

    // 다중 스케줄(요일·시간·강의실) 수정용
    private List<ScheduleDTO> scheduleList;

    @Data
    public static class ScheduleDTO {
        @NotBlank(message = "요일은 필수 입력값입니다.")
        private String dayOfWeek;

        @NotBlank(message = "시간은 필수 입력값입니다.")
        private String time;

        @NotBlank(message = "강의실은 필수 입력값입니다.")
        private String place;
    }
}
