package com.ac.kr.academy.domain.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subject {
    private Long id;
    private String name;
    private Integer credit; // 이수학점
    private String description;
    private Long professorId;   //담당교수 id / fk
    private Long deptId;        //학과 id / fk
}
