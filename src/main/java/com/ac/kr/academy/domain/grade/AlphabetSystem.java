package com.ac.kr.academy.domain.grade;

import lombok.Data;

@Data
public class AlphabetSystem {

    //관리자(직원)용 점수(A+,A,B+...) 비율 도메인
    private Long id;
    private Long subjectId;
    private Long enrollmentId;  // Null = 전체설정, 특정 과목 커스텀은 subjectId
    private String alphabet;    // 점수(A+,A,B+..)
    private Double boundary;    // 점수(A+,A,B+..) 비율 (%)
}
