package com.ac.kr.academy.domain.grade;

import lombok.Data;

@Data
public class Grade {
    private Long id;
    private String alphabet;
    private Long gpa;           //평균 학점
    private Long score;         //취득 학점
    private Integer totalInt;   //총점(자연수/90점)
    private String subjectName; // 과목명
    private String studentNo;   // 학번
    private String studentName; // 학생명

    private Integer midExam;    // 중간고사 점수
    private Integer finalExam;  // 기말고사 점수
    private Integer assignment; // 과제 점수
    private Integer attendance; // 출석 점수

    private Long studentId;     //fk
    private Long enrollmentId;  //fk
}
