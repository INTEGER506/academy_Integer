package com.ac.kr.academy.dto.course;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDayTimeDTO {

    private String dayOfWeek;
    private String time;
}
