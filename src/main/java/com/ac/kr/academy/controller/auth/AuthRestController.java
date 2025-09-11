package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.dto.auth.ChangePasswordDTO;
import com.ac.kr.academy.dto.auth.JwtResponseDTO;
import com.ac.kr.academy.dto.auth.LoginRequestDTO;
import com.ac.kr.academy.security.CustomUserDetails;
import com.ac.kr.academy.security.jwt.JwtTokenProvider;
import com.ac.kr.academy.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 사용자 인증 관련 API
 * - 로그인
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthRestController {

    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequestDTO loginRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        //임시 비밀번호 여부 확인
        boolean isTempPassword = ((CustomUserDetails) auth.getPrincipal()).getUser().isPasswordTemp();

        //인증 성공 시 토큰 발급
        String accessToken = tokenProvider.generateAccessToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(auth);

        //토큰과 임시비번 상태를 클라이언트에게 응답
        return ResponseEntity.ok(
                JwtResponseDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .isTempPassword(isTempPassword)
                        .build()
        );
    }

    //비밀번호 변경
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO, Authentication auth){
        String username = auth.getName();
        try{
            userService.changePassword(username,
                    changePasswordDTO.getCurrentPassword(),
                    changePasswordDTO.getNewPassword());
            return ResponseEntity.ok("비밀번호 변경을 완료했습니다.");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
