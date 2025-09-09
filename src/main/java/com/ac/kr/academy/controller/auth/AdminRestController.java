package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN')")   //관리자만 접근가능
    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestParam String username,
                                           @RequestParam String email,
                                           @RequestParam String name){
        //임시 비밀번호 자동 생성


    }

}
