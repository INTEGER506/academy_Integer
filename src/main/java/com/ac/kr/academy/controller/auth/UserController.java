package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 비밀번호 변경 컨트롤러
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;




}
