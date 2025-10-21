package com.ac.kr.academy.domain.grade;

import lombok.Data;

@Data
public class AlphabetSystem {

    //관리자(직원)용 점수(A+,A,B+...) 비율 도메인
    private Long id;
    private Long courseId;      // Null = 글로벌설정, 특정 강의 커스텀은 courseId
    private Long subjectId;     // Null = 글로벌설정, 특정 과목 커스텀은 subjectId
    private String alphabet;    // 점수(A+,A,B+..)
    private Double boundary;    // 해당 학점을 받을 수 있는 학생 비율 (%) - 퍼센트 기반
    private String description; // 설명 (선택사항)
}
