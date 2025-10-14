package com.ac.kr.academy.domain.grade;

import lombok.Data;



@Data
public class GradeSystem {

    // 교수용 점수 비율 도메인
    private Long id;
    private Double midExamRatio;    // 중간 %
    private Double finalExamRatio;  // 기말 %
    private Double assignmentRatio; // 과제 %
    private Double attendanceRatio; // 출석 %
    private Long courseId;          // FK
}
