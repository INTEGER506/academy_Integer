package com.ac.kr.academy.controller.notification;

import com.ac.kr.academy.domain.notification.Notification;
import com.ac.kr.academy.security.CustomUserDetails;
import com.ac.kr.academy.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

    //미확인 알림 개수
//    @GetMapping("/unread/{targetId}")
//    public ResponseEntity<?> getUnreadCount(@PathVariable Long targetId){
//        int count = notificationService.countUnread(targetId);
//        return ResponseEntity.ok(count);
//    }

    //읽음 상태로 변경 (클릭 시)
    @Transactional
    @PostMapping("/{notiId}/read")
    public ResponseEntity<?> markAsResolved(@PathVariable Long notiId, @AuthenticationPrincipal CustomUserDetails userDetails){
        log.info("notiId= " + notiId);

        if (userDetails == null) {
            return ResponseEntity.status(401).build(); // 인증 실패
        }

        Long targetId = userDetails.getUser().getId();

        notificationService.markAsResolved(notiId, targetId);
        return ResponseEntity.ok().build();
    }

    //특정 사용자의 읽은 알림 전체 삭제
    @DeleteMapping("/resolved") // 1. 경로에서 {targetId}를 제거
    public ResponseEntity<Void> removeResolvedByUserId(@AuthenticationPrincipal CustomUserDetails userDetails){ // 2. @AuthenticationPrincipal 사용

        if (userDetails == null) {
            return ResponseEntity.status(401).build(); // 인증 실패
        }

        Long targetId = userDetails.getUser().getId(); // 3. 로그인 정보에서 targetId 획득

        notificationService.removeResolvedByUserId(targetId);
        return ResponseEntity.ok().build();
    }

//    @DeleteMapping("/resolved/{targetId}")
//    public ResponseEntity<Void> removeResolvedByUserId(@PathVariable Long targetId){
//        notificationService.removeResolvedByUserId(targetId);
//        return ResponseEntity.ok().build();
//    }
}