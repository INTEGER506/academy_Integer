package com.ac.kr.academy.dto.course;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDayTimeDTO {

    private Long courseId;
    private String subjectName;
    private String professorName;
    private String dayOfWeek;
    private String time;
    private String startTime;
    private String endTime;
    private String place;
}
