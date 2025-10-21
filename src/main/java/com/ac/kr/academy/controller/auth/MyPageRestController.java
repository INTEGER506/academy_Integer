package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.domain.user.User;
import com.ac.kr.academy.dto.auth.UpdateUserRequestDTO;
import com.ac.kr.academy.dto.user.MyPageInfoDTO;
import com.ac.kr.academy.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageRestController {

    private final UserService userService;

    /**
     * ✅ 내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(Authentication auth) {
        String username = auth.getName();
        User user = userService.findByUsername(username);

       MyPageInfoDTO myPageInfoDTO = userService.findMyPageInfo(user.getId(), user.getRole());

        return ResponseEntity.ok(myPageInfoDTO);
    }

    /**
     * ✅ 내 정보 수정
     */
    @PutMapping("/update-me")
    public ResponseEntity<?> updateMyInfo(@RequestBody MyPageInfoDTO myPageInfoDTO, Authentication auth) {
        //로그인된 사용자 정보 확인
        String username = auth.getName();
        User loggedInUser = userService.findByUsername(username);

        //보안 검증
        if (!loggedInUser.getId().equals(myPageInfoDTO.getUserId())) {
            log.warn("접근 위반 시도: 로그인한 사용자 {}가 사용자 ID {}를 수정하려고 했습니다.",
                    loggedInUser.getId(), myPageInfoDTO.getUserId());
            return ResponseEntity.status(403).body("사용자 ID가 일치하지 않습니다.");
        }

        try{
            userService.updateUserBasicInfo(myPageInfoDTO);
            return ResponseEntity.ok().body("기본 정보가 성공적으로 업데이트 되었습니다.");
        } catch (Exception e){
            log.error("사용자 {}에 대한 기본 사용자 정보 업데이트에 실패: {}", loggedInUser.getId(), e.getMessage());
            return ResponseEntity.internalServerError().body("업데이트 실패: " + e.getMessage());
        }
    }
}
