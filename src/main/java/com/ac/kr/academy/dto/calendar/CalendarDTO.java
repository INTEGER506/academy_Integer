package com.ac.kr.academy.dto.calendar;


import lombok.Data;

import java.sql.Date;

@Data
public class CalendarDTO {

    private Long id;
    private String title;
    private String content;
    private Date startDate;
    private Date endDate;
    private Date createdAt;
    private Long userId;

}
