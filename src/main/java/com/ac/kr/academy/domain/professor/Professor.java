package com.ac.kr.academy.domain.professor;


import lombok.Data;

import java.sql.Date;

@Data
public class Professor {
    private Long id;
    private Long professorNum;
    private Date createdAt;
    private Date endedAt;
    private Long deptId;
    private Long userId;
}
