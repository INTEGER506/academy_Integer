package com.ac.kr.academy.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 로그인 테스트
 * - 관리자 비밀번호 암호화하기 위해 생성
 * */

@Slf4j
public class AdminLoginTest {
    public static void main(String[] args){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String testPassword = "admin123";   //암호화할 비밀번호
        String encodedPassword = passwordEncoder.encode(testPassword);
        log.info("testPassword:{}", encodedPassword);
    }
}
//$2a$10$ShqY671MqCXxZPqcSTwk9O4Cde.bUwuXqSj6tVkeTs.b5dW7pYnwm