package com.ac.kr.academy.mapper.notification;

import com.ac.kr.academy.domain.notification.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    //새 알림 생성
    void insertNotification(Notification notification);

    //알림 목록 조회
    List<Notification> getNotificationsByUserId(@Param("targetId") Long targetId);

    //미확인 알림 개수 조회
    int countUnresolved(Long targetId);

    //알림 확인 상태로 변경
    void markAsResolved(Long notiId);

    //읽은 알림 전체 삭제
    void deleteResolvedByUserId(@Param("targetId") Long targetId);

}
