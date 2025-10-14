package com.ac.kr.academy.domain.user;

import lombok.Data;

@Data
public class Advisor {
    private Long id;
    private Long professorId;
    private Long studentId;
}
