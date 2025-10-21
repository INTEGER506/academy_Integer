package com.ac.kr.academy.controller.notification;

import com.ac.kr.academy.domain.notification.Notification;
import com.ac.kr.academy.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationRestController {
    private final NotificationService notificationService;

    //알림 탭(화면) 전체 조회
    @GetMapping("/{targetId}")
    public ResponseEntity<?> getNotifications(@PathVariable Long targetId){
        List<Notification> notifications = notificationService.getNotificationList(targetId);
        return ResponseEntity.ok(notifications);
    }

    //미확인 알림 개수 - 종 모양에 표시
    @GetMapping("/unread/{targetId}")
    public ResponseEntity<?> getUnreadCount(@PathVariable Long targetId){
        int count = notificationService.countUnread(targetId);
        return ResponseEntity.ok(count);
    }

    //읽음 상태로 변경 (클릭 시)
    @PutMapping("/{notiId}/read")
    public ResponseEntity<?> markAsResolved(@PathVariable Long notiId){
        notificationService.markAsResolved(notiId);
        return ResponseEntity.ok().build();
    }

    //특정 사용자의 읽은 알림 전체 삭제
    @DeleteMapping("/resolved/{targetId}")
    public ResponseEntity<Void> removeResolvedByUserId(@PathVariable Long targetId){
        notificationService.removeResolvedByUserId(targetId);
        return ResponseEntity.ok().build();
    }
}
