package com.ac.kr.academy.domain.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
public class Student {
    private Long id;                // DB 내에서 행을 식별하기 위한 고유키
    private String studentNum;      // 학번 (연도+학과+순서)
    private String status;          // 상태 (재학중, 휴학중, 졸업) / DEFAULT 'pending'
    private LocalDate createdAt;     // 입학일
    private LocalDate endedAt;      // 졸업일
    private int enrolledCredits;   // 신청 학점
    private Long deptId;            // FK
    private Long userId;            // FK
}
