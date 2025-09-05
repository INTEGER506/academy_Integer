package com.ac.kr.academy.domain.course;


import lombok.Data;

import java.sql.Date;

@Data
public class Course {
    private Long id;
    private String place;
    private Date dayOfWeek;
    private Date stratPeriod;
    private Date endPeriod;
    private Long capacity;
    private Long semesterId;
    private Long subjectId;
}
