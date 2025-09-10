package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.domain.user.User;
import com.ac.kr.academy.dto.auth.ChangePasswordDTO;
import com.ac.kr.academy.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 비밀번호 변경 컨트롤러
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserRestController {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO, Authentication auth) {
        String username = auth.getName();
        User user = userMapper.findByUsername(username);
        if(!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("잘못된 비밀번호 입니다.");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        user.setPasswordTemp(false);    //임시비번 아님으로 변경
        userMapper.updateUser(user);
        return ResponseEntity.ok("비밀번호를 변경을 완료했습니다.");
    }

}
