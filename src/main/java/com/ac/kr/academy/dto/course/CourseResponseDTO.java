package com.ac.kr.academy.dto.course;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDTO {
    private Long id;
    private String place;
    private String dayOfWeek;
    private Integer capacity;
    private Long semesterId;
    private String semesterName;
    private Long subjectId;
    private String subjectName;
    private Integer subjectCredit;
    private String title;
    private String department;
    private Integer year;
    private String description;
    private String professorName;
}
