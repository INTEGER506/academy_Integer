package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.domain.auth.RefreshToken;
import com.ac.kr.academy.domain.user.User;
import com.ac.kr.academy.dto.auth.ChangePasswordDTO;
import com.ac.kr.academy.dto.auth.FindPasswordDTO;
import com.ac.kr.academy.dto.auth.JwtResponseDTO;
import com.ac.kr.academy.dto.auth.LoginRequestDTO;
import com.ac.kr.academy.security.CustomUserDetails;
import com.ac.kr.academy.security.jwt.JwtTokenProvider;
import com.ac.kr.academy.service.auth.RefreshTokenService;
import com.ac.kr.academy.service.log.LogHistoryService;
import com.ac.kr.academy.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;


/**
 * 사용자 인증 관련 API
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthRestController {

    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final LogHistoryService logHistoryService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequestDTO loginRequestDTO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        try{

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
            );

            //임시 비밀번호 여부 확인
            boolean tempPassword = ((CustomUserDetails) auth.getPrincipal()).getUser().isPasswordTemp();

            //인증 성공 시 토큰 발급
            String accessToken = tokenProvider.generateAccessToken(auth);
            String refreshToken = tokenProvider.generateRefreshToken(auth);

            //접속 기록 저장 로직
            User user = userService.findByUsername(loginRequestDTO.getUsername());  //로그인한 사용자 정보
            String ipAddress = request.getRemoteAddr(); //ip 주소 가져오기
            logHistoryService.saveLoginLog(user.getId(), user.getUsername(), ipAddress);

            //refresh token DB 저장 - 사용자당 1개만 유지
            refreshTokenService.saveRefreshToken(
                    user.getId(), refreshToken,
                    LocalDateTime.now().plusSeconds(tokenProvider.getRefreshTokenValiditySeconds())
            );

            //access token 쿠키에 담기
            Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", accessToken);
            accessTokenCookie.setHttpOnly(true);    // 자바스크립트 접근 방지 (XSS 공격 방어)
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(3600);      //1시간
            response.addCookie(accessTokenCookie);

            //토큰과 임시비번 상태를 클라이언트에게 응답
            return ResponseEntity.ok(
                    JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .tempPassword(tempPassword)
                            .build()
            );
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("로그인 에러 발생: " + e.getMessage());
        }
    }

    //비밀번호 변경 - 성공 시 쿠키 무효화
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO, Authentication auth, HttpServletResponse response){
        String username = auth.getName();

        if(changePasswordDTO.getNewPassword().length() < 8){
            return ResponseEntity.badRequest().body("새 비밀번호는 최소 8자 이상이어야 합니다.");
        }

        try{
            userService.changePassword(username,
                    changePasswordDTO.getCurrentPassword(),
                    changePasswordDTO.getNewPassword());

            //쿠키 무효화(삭제)
            deleteCookie(response, "JSESSIONID");
            deleteCookie(response, "ACCESS_TOKEN");

            return ResponseEntity.ok("비밀번호 변경을 완료했습니다.");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //비밀번호 찾기
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestBody FindPasswordDTO request){
        if(request.getNewPassword().length() < 8){
            return ResponseEntity.badRequest().body("새 비밀번호는 최소 8자 이상이어야 합니다.");
        }

        boolean success = userService.resetPasswordByUserInfo(
                request.getUsername(), request.getName(), request.getEmail(), request.getNewPassword());

        if(success){
            return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다. 새 비밀번호로 로그인해주세요.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("입력하신 정보가 일치하지 않습니다.");
        }
    }

    //로그아웃 - 접속기록관리용
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication auth, HttpServletRequest request, HttpServletResponse response){
        if(auth != null && auth.isAuthenticated()){
            String username = auth.getName();
            User user = userService.findByUsername(username);
            if(user != null){
                logHistoryService.userLogoutTime(user.getId());
            }
        }

        //쿠키 무효화 로직 추가
        //보안상 무효화
        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }

        deleteCookie(response, "JSESSIONID");
        deleteCookie(response, "ACCESS_TOKEN");

        return ResponseEntity.ok("로그아웃 성공 및 쿠키 삭제 명령 전달");
    }

    //Refresh Token을 통한 Access Token 재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request){
        String refreshToken = request.get("refreshToken");

        //DB 확인
        RefreshToken savedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        //JWT 유효성 검증 + 만료 체크
        if(!tokenProvider.validateToken(refreshToken)){
            refreshTokenService.deleteByUserId(savedToken.getUserId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or Refresh token expired");
        }

        //사용자 정보 조회
        String username = tokenProvider.getUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        //새 Access Token 발급 (Refresh Token 그대로 유지)
        String newAccessToken = tokenProvider.generateAccessToken(auth);

        return ResponseEntity.ok(JwtResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tempPassword(((CustomUserDetails) userDetails).getUser().isPasswordTemp())
                .build()
        );
    }

    //쿠키 무효화 유틸리티 메소드 -> 로그아웃, 비밀번호 변경 메소드
    private void deleteCookie(HttpServletResponse response, String name){
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);    //만료 시간을 0으로 설정 - 즉시 만료
        cookie.setPath("/");    //쿠키 경로 설정 - 전체 도메인에서 삭제
        response.addCookie(cookie);
    }
}