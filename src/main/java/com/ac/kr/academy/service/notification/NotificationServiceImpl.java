package com.ac.kr.academy.service.notification;

import com.ac.kr.academy.domain.notification.Notification;
import com.ac.kr.academy.mapper.notification.NotificationMapper;
import com.ac.kr.academy.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    //생성
    @Override
    public void createNotification(Notification notification) {
        notificationMapper.insertNotification(notification);
    }

    //목록 조회
    @Override
    public List<Notification> getNotificationList(Long targetId) {
        return notificationMapper.getNotificationsByUserId(targetId);
    }

    //미확인 알림 개수 조회
    @Override
    public int countUnread(Long targetId) {
        return notificationMapper.countUnresolved(targetId);
    }

    //알림 확인 상태로 변경
    @Override
    public void markAsRead(Long notiId) {
        notificationMapper.markAsResolved(notiId);
    }

    /**
     * User 도메인, 인터페이스 매퍼 확인 후 수정 필요
     * */
    //긴급(중요) 공지 알림 모든 사용자에게 생성(발송)
    @Override
    public void sendAllUser(Long noticeId, String noticeTitle) {
        List<Long> allUserId = userMapper.findAllUserIds();

        for(Long userId : allUserId){
            Notification notification = new Notification();
            notification.setNotiType("urgent_notice");
            notification.setTitle(noticeTitle); //jsp에서 긴급표시 해주기를..
            notification.setTargetId(userId);
            notification.setNoticeId(noticeId);

            notificationMapper.insertNotification(notification);
        }
    }

    //읽은 알림 전체 삭제
    @Override
    public void removeResolved() {
        notificationMapper.deleteResolved();
    }

//    @Override
//    public void sendNotification(String notiType, List<Long> targetuserId, String title, Long relatedId) {
//        for(Long userId : targetuserId){
//            Notification notification = new Notification();
//            notification.setNotiType(notiType);
//            notification.setTitle(title);
//            notification.setTargetId(userId);
//
//            //관련 ID를 범용 필드로 사용
//            if("urgent_notice".equals(notiType) || "grade_announcement".equals(notiType)){
//                notification.setRelatedId(relatedId);
//            }
//
//            notificationMapper.insertNotification(notification);
//
//        }
//    }
}
