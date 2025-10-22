package com.ac.kr.academy.dto.course;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    // 다중 요일/시간 입력 지원용
    private List<ScheduleDTO> scheduleList;

    // 내부 클래스 (각 요일/시간 세트를 담음)
    @Data
    public static class ScheduleDTO {
        private String dayOfWeek;
        private String time;
        private String place;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
