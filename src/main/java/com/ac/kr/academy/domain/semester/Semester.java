package com.ac.kr.academy.domain.semester;

import lombok.Data;

@Data
public class Semester {
    private Long id;    //1~8학기
    private String name;
    private Integer year;
}
