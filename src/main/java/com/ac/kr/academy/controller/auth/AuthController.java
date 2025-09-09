package com.ac.kr.academy.controller.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * JSP 생성 후 수정 필요
 * */

@Slf4j
@Controller
public class AuthController {

    //기본 루트 접속시 로그인페이지로 이동
    @GetMapping("/")
    public String home(){
        return "redirect:/auth/login";
    }

    @GetMapping("/auth/login")
    public String loginPage(){
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String registerPage(){
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String registerSubmit(){
        log.info("회원가입 요청 처리 완료");
        return "redirect:/auth/login";
    }

    //로그인 성공 시 대시보드로 이동
    @GetMapping("/auth/login-success")
    public String loginSuccessPage(){
        return "dashboard";
    }
}
