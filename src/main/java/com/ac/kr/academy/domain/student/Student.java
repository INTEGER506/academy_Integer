package com.ac.kr.academy.domain.student;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Long id;
    private String studentNum;
    private String status;
    private LocalDate createdAt;
    private LocalDate endedAt;
    private Long deptId;
    private Long userId;
}
