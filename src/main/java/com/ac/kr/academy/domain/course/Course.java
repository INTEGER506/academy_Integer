package com.ac.kr.academy.domain.course;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Long id;
    private Long professorId;
    private Long subjectId;
    private Long semesterId;
    private Integer capacity;
    private Integer numOfStudents;
    private String dayOfWeek;
    private String place;
    private String status; //강의 상태
}
