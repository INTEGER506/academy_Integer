package com.ac.kr.academy.service.notification;

import com.ac.kr.academy.domain.notice.Notice;
import com.ac.kr.academy.domain.notification.Notification;
import com.ac.kr.academy.mapper.notice.NoticeMapper;
import com.ac.kr.academy.mapper.notification.NotificationMapper;
import com.ac.kr.academy.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;
    private final NoticeMapper noticeMapper;    //deadline(미리알림 기능)

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
    public void markAsResolved(Long notiId) {
        notificationMapper.markAsResolved(notiId);
    }

    //읽은 알림 전체 삭제
    @Override
    public void removeResolvedByUserId(Long targetId) {
        notificationMapper.deleteResolvedByUserId(targetId);
    }

    /**
     * 여러 알림 유형 처리할 범용 메서드
     * - 매개변수 : targetUserId, relatedId
     */
    @Override
    public void sendNotification(String notiType, List<Long> targetUserId, String title, Long relatedId) {
        for(Long userId : targetUserId){
            Notification notification = new Notification();
            notification.setNotiType(notiType);
            notification.setTitle(title);
            notification.setTargetId(userId);

            //새로운 알림 추가해야할 경우 여기에 추가
            if("urgent_notice".equals(notiType) || "deadline_reminder".equals(notiType)){
                notification.setNoticeId(relatedId);
            } else if ("grade_announcement".equals(notiType)) {
                notification.setGradeId(relatedId);
            }
            notificationMapper.insertNotification(notification);
        }
    }

    //긴급(중요) 공지 알림 모든 사용자에게 생성(발송)
    @Override
    public void sendAllUser(Long noticeId, String noticeTitle) {
        List<Long> allUserId = userMapper.findAllUserIds();

        //범용 메서드 호출
        sendNotification("urgent_notice", allUserId, noticeTitle, noticeId);
    }

    //데드라인(미리알림 기능) - 매일 오전 8시에 실행
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDeadLine(){
        List<Notice> allNotices = noticeMapper.getAllNotices();
        LocalDate today = LocalDate.now();

        List<Notice> deadlineNotice = allNotices.stream()
                .filter(notice -> notice.getDeadline() != null &&
                        (notice.getDeadline().isEqual(today) || notice.getDeadline().isBefore(today)) &&    //서버 장애 대비 isBefore
                        notice.getIsSent() == 0)
                .collect(Collectors.toList());

        List<Long> allUserIds = userMapper.findAllUserIds();    //모든 사용자 ID 한번만 조회

        for(Notice notice : deadlineNotice){
            String notificationTitle = "마감 알림: " + notice.getTitle();

            //범용 메서드 호출
            sendNotification("deadline_reminder", allUserIds, notificationTitle, notice.getId());
            noticeMapper.updateDeadlineStatus(notice.getId());
        }
    }
}
