package com.ac.kr.academy.domain.user;

import lombok.Data;

import java.sql.Date;

@Data
public class Professor {
    private Long id;                // 고유 ID
    private String professorNum;    // 교수번호
    private Date createAt;          // 입사일
    private Date endedAt;           // 퇴사일
    private Long deptId;            // FK
    private Long userId;            // FK
}
