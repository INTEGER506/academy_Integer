package com.ac.kr.academy.dto.course;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseListResponseDTO {
    private Long id;
    private Long professorId;
    private String subjectName;
    private String professorName;
    private String semesterName;
    private Integer capacity;
    private Integer numOfStudent;
    private String dayOfWeek;
    private String time;
    private String place;
    private String status;
    private Integer credit;
}
