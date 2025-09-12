package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.domain.user.User;
import com.ac.kr.academy.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자 전용 API
 * - 로그인 ID(username), 임시 비밀번호 생성
 * - 비밀번호 초기화
 */

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminRestController {

    private final UserService userService;

    //로그인 ID 자동생성
    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestParam String role,
                                           @RequestParam(required = false) Long deptId, //파라미터가 없어도 에러X
                                           @RequestParam String email,
                                           @RequestParam String name){
        try{
            User createUser = userService.createUser(role, deptId, email, name);
            return ResponseEntity.ok("계정 생성 완료, 로그인 ID: " + createUser.getUsername());
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //비밀번호 초기화
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String username){
        try{
            String tempPassword = userService.resetPassword(username);
            return ResponseEntity.ok(username + "의 비밀번호가 초기화 되었습니다. 새 임시 비밀번호: " + tempPassword);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //사용자 전체 조회
    @GetMapping("/users/all")
    public ResponseEntity<?> getAllUsers(){
        List<User> userList = userService.getAllUsers();
        if(userList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("등록된 사용자가 없습니다.");
        }
        return ResponseEntity.ok(userList);
    }

    //권한별 사용자 조회
    @GetMapping("/users/role")
    public ResponseEntity<?> getUsersByRole(@RequestParam String role){
        List<User> userList = userService.getUsersByRole(role);
        if(userList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 권한을 가진 사용자가 없습니다.");
        }
        return ResponseEntity.ok(userList);
    }

    //사용자 정보 수정
    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                        @RequestBody User user,
                                        @RequestBody Object roleEntity){
            if(!userId.equals(user.getId())){
                return ResponseEntity.badRequest().body("ID가 일치하지 않습니다.");
            }
            userService.updateUser(user, roleEntity);
            return ResponseEntity.ok("사용자 정보가 수정되었습니다.");
    }

    //사용자 삭제
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId){
        try{
            User user = userService.findById(userId);
            if(user == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }
            userService.deleteUser(user);
            return ResponseEntity.ok("사용자 삭제를 완료했습니다.");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 삭제 중 오류가 발생했습니다.");
        }
    }

    //학생 상태값 변경 (재학중, 휴학중, 졸업)
    @PutMapping("/user/{userId}/status")
    public ResponseEntity<?> updateStudentStatus(@PathVariable Long userId, @RequestParam String status){
        userService.updateStudentStatus(userId, status);
        return ResponseEntity.ok("사용자 iD " + userId + "의 상태를 '" + status + "'로 변경 완료했습니다.");
    }
}
