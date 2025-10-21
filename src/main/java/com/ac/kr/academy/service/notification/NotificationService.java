package com.ac.kr.academy.service.notification;

import com.ac.kr.academy.domain.notification.Notification;

import java.util.List;

public interface NotificationService {
    //생성
    void createNotification(Notification notification);

    //전체 목록 조회 (알림 센터)
    List<Notification> getNotificationList(Long targetId);

    //미확인 알림 개수 조회
    int countUnread(Long targetId);

    //알림 확인 상태로 업데이트
    void markAsResolved(Long notiId);

    //읽은 알림 전체 삭제
    void removeResolvedByUserId(Long targetId);


    //모든 사람에게 알림 발송 / 긴급(중요) 공지
    void sendAllUser(Long noticeId, String noticeTitle);

    //여러 알림 유형 처리할 범용 메서드
    void sendNotification(String notiType, List<Long> targetUserId, String title, Long relatedId);

}
