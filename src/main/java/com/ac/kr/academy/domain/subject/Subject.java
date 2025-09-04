package com.ac.kr.academy.domain.subject;

import lombok.Data;

@Data
public class Subject {
    private Long id;
    private String name;
    private Integer credit; // 이수학점
    private Long professorId;   //담당교수 id / fk
    private Long deptId;        //학과 id / fk
}
