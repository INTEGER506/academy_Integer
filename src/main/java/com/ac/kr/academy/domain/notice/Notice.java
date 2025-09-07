package com.ac.kr.academy.domain.notice;

import lombok.Data;

import java.util.Date;

@Data
public class Notice {
    private Long id;
    private String title;
    private String content;
    private Date startDate; //시작일자
    private Date endDate;   //종료일자
    private Date createdAt; //생성일
    private Date deadline;  //미리알림(연/월/일)
    private int viewCount;  //조회수
    private int isUrgent;   //긴급공지 여부(1:긴급, 0:일반)
}
