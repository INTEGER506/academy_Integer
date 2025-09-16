package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.domain.user.User;
import com.ac.kr.academy.dto.auth.UpdateUserRequestDTO;
import com.ac.kr.academy.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageRestController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(Authentication auth){
        String username = auth.getName();
        User user = userService.findByUsername(username);

        Object roleEntity = userService.findByRole(user.getId(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("role", roleEntity);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-me")
    public ResponseEntity<?> updateMyInfo(@RequestBody UpdateUserRequestDTO requestDTO, Authentication auth){
        User user = requestDTO.getUser();
        Object roleEntity = requestDTO.getRoleEntity();

        if(!auth.getName().equals(user.getUsername())){
            return ResponseEntity.badRequest().body("username 불일치");
        }
        userService.updateUser(user, roleEntity);
        User updatedUser = userService.findByUsername(auth.getName());
        return ResponseEntity.ok(updatedUser);
    }
}
