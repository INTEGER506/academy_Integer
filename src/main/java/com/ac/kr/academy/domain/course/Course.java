package com.ac.kr.academy.domain.course;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Long id;
    private String place;
    private String dayOfWeek;
    private Integer capacity;
    private Long semesterId;
    private Long subjectId;
    private String title;
    private String department;
    private Integer year;
    private String description;
    private Long professorId;
}
