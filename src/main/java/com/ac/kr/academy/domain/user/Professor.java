package com.ac.kr.academy.domain.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
public class Professor {
    private Long id;                // 고유 ID
    private String professorNum;    // 교수번호
    private LocalDate createdAt;          // 입사일
    private LocalDate endedAt;           // 퇴사일
    private Long deptId;            // FK
    private Long userId;            // FK
}
