package com.ac.kr.academy.domain.graduation;

import lombok.Data;

import java.util.Date;

@Data
public class Graduation {
    private Long id;
    private String requirementsStatus;  //졸업요건(학점) 자동체크 결과 (met: 학점 충족 / not_met: 학점 미충족)
    private String status;      //졸업 심사에 따른 상태값('approved', 'postponed', 'completed', 'rejected')
    private Date createdAt;
    private Long advisorId;
    private Long studentId;
}
