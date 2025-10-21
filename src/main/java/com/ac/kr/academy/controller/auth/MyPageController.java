package com.ac.kr.academy.controller.auth;

import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Data
@Controller
public class MyPageController {

    @GetMapping("/mypage")
    public String mypage() {
        return "mypage/mypage";
    }

    @GetMapping("/admin/mypage")
    public String adminMypage() {
        return "mypage/mypage"; // 같은 JSP 공유 가능
    }

    @GetMapping("/professor/mypage")
    public String professorMypage() {
        return "mypage/mypage";
    }

    @GetMapping("/student/mypage")
    public String studentMypage() {
        return "mypage/mypage";
    }
}

