package com.ac.kr.academy.dto.course;


import com.ac.kr.academy.domain.dept.Dept;
import com.ac.kr.academy.domain.professor.Professor;
import com.ac.kr.academy.domain.semester.Semester;
import com.ac.kr.academy.domain.subject.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseListResponseDTO {
    private Long courseId;
    private String place;
    private String dayOfWeek;
    private Integer capacity;
    private String time;
    private Subject subject;
    private Professor professor;
    private Semester semester;
    private Dept dept;
}
