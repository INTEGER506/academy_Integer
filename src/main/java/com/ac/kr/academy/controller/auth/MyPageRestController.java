package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.domain.user.User;
import com.ac.kr.academy.dto.auth.UpdateUserRequestDTO;
import com.ac.kr.academy.dto.user.MyPageInfoDTO;
import com.ac.kr.academy.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.proxy.annotation.Post;
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
    public ResponseEntity<?> getMyInfo(Authentication auth){
        String username = auth.getName();
        User user = userService.findByUsername(username);

        Object roleEntity = userService.findMyPageInfo(user.getId(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("role", roleEntity);

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 내 정보 수정
     */
    @PutMapping("/update-me")
    public ResponseEntity<?> updateMyInfo(@RequestBody MyPageInfoDTO myPageInfoDTO, Authentication auth){
        String username = auth.getName();
        User loggedInUser = userService.findByUsername(username);

        //요청 DTO에 userId 강제 주입
        myPageInfoDTO.setUserId(loggedInUser.getId());

        try{
            userService.updateUserBasicInfo(myPageInfoDTO);
            return ResponseEntity.ok().body("정보가 성공적으로 업데이트 되었습니다.");
        } catch (Exception e){
            log.error("사용자 {}의 마이페이지 정보 수정중 오류 발생: {}", username, e.getMessage());
            return ResponseEntity.badRequest().body("정보 수정 실패: " + e.getMessage());
        }
    }
}