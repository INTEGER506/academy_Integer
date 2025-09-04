package com.ac.kr.academy.domain.notification;

import lombok.Data;

import java.util.Date;

@Data
public class Notification {
    private Long notiId;
    private String notiType;
    private Long targetId;  //user id
    private Date createAt;
    private String title;
    private int isResolved; //0:미확인, 1:확인
    private Long gradeId;   //성적 / fk
    private Long noticeId;  //공지 / fk
}
