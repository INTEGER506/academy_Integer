package com.ac.kr.academy.domain.notification;

import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class Notification {
    private Long notiId;
    private String notiType;
    private Long targetId;  //user id
    private LocalDate createdAt;
    private String title;
    private int isResolved; //0:미확인, 1:확인
    private Long relatedId; //모든 알림에 대한 범용적인 관련 ID - sendNotification
    private Long gradeId;   //성적 / fk
    private Long noticeId;  //공지 / fk

    //jsp 에서는 LocalDate 사용 불가로 String 으로 변환
    public String getCreatedAtStr() {
        return createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
    }
}
