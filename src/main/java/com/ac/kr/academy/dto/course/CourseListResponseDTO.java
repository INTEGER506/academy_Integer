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
