package com.ac.kr.academy.controller.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * JSP 페이지 이동 역할
 */

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {

    //기본 루트 접속시 로그인페이지로 이동
    @GetMapping("/")
    public String home(){
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerSubmit(){
        log.info("회원가입 요청 처리 완료");
        return "redirect:/auth/login";
    }

    //로그인 성공 시 대시보드로 이동
    @GetMapping("/login-success")
    public String loginSuccessPage(){
        return "dashboard";
    }

    //비밀번호 변경 페이지로 이동
    @GetMapping("/change-password")
    public String changePasswordPage(){
        return "auth/change-password";
    }
}
