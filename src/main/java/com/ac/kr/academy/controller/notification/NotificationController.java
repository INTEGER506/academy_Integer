package com.ac.kr.academy.controller.notification;

import com.ac.kr.academy.domain.notification.Notification;
import com.ac.kr.academy.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * jsp 만들기 전 / 임시로 이름 지음

 * principal 객체 사용해서 사용자 현재 접속한 사용자 ID 확인
 * securityUtil도 가능 -> 해당 컨트롤러에서는 이게 더 수정하기 편함

 * */
@Controller
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    //전체 목록 조회 (알림 센터)
    @GetMapping("/center")
    public String getNotificationList(Model model){
        //임시 사용자 ID (나중에 수정 필요)
        Long currentUserId = 1L; // principal 객체 또는 securityUtil 사용 수정

        List<Notification> notificationList = notificationService.getNotificationList(currentUserId);
        model.addAttribute("notificationList", notificationList);

        return "notification/center";
    }

}
