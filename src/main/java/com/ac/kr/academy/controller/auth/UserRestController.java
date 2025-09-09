package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.domain.user.User;
import com.ac.kr.academy.dto.auth.JwtResponse;
import com.ac.kr.academy.dto.auth.LoginRequest;
import com.ac.kr.academy.dto.auth.RegisterRequest;
import com.ac.kr.academy.mapper.user.UserMapper;
import com.ac.kr.academy.security.CustomUserDetails;
import com.ac.kr.academy.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * username 만들어서 주니까 회원가입 필요 없음
 * 관리자용 계정 생성 API (username, 임시 비밀번호)
 * 임시 비밀번호 강제 변경 로직
 * */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserRestController {

    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        //인증 성공 시 토큰 발급
        String accessToken = tokenProvider.generateAccessToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(auth);

        //클라이언트에게 JwtResponse 반환
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
    }

//    @PostMapping("/register")
//    public ResponseEntity<?> register(@Validated @RequestBody RegisterRequest registerRequest) {
//        //이메일 중복 체크
//        if(registerRequest.getEmail() != null) {
//            return ResponseEntity.badRequest().body(Map.of("error", "이미 사용중인 이메일 입니다."));
//        }
//
//        User user = new User();
//        user.setEmail(registerRequest.getEmail());
//        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
//        user.setPhone(registerRequest.getPhone());
//        user.setName(registerRequest.getName());
//
//        //username 기반으로 역할을 추론하여 권한 부여
//        String role = determinRoleFromUsername(registerRequest.getUsername());
//        user.setRole(role);
//
//        userMapper.insertUser(user);
//
//        //가입 직후 로그인 된 상태로 만들기
//        Authentication auth = new UsernamePasswordAuthenticationToken(
//                new CustomUserDetails(user, CustomUserDetails.),
//                null,
//                CustomUserDetails.toAuthorities(user)
//        );
//
//        String accessToken = tokenProvider.generateAccessToken(auth);
//        String refreshToken = tokenProvider.generateRefreshToken(auth);
//
//        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
//    }
//
//    private String determinRoleFromUsername(String username) {
//        Pattern studentPattern = Pattern.compile("^\\d{9}$");
//    }
}
