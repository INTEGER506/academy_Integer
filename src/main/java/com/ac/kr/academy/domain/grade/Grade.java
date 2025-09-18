package com.ac.kr.academy.domain.grade;

import lombok.Data;

@Data
public class Grade {
    private Long id;
    private String alphabet;
    private Long gpa;       //평균 학점
    private Long score;     //취득 학점
    private Integer totalInt;   //총점(자연수/90점)
    private Long studentId;     //fk
    private Long enrollmentId;  //fk
}
