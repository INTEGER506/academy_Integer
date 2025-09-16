package com.ac.kr.academy.dto.course;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseListResponseDTO {
    private Long id;
    private Long professorId;
    private String subjectName;
    private String professorName;
    private String semesterName;
    private int capacity;
    private int numOfStudent;
    private String dayOfWeek;
    private String time;
    private String place;
    private String status;
    private int credit;
}
