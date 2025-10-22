package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.domain.log.LogHistory;
import com.ac.kr.academy.domain.user.User;
import com.ac.kr.academy.dto.auth.UpdateUserRequestDTO;
import com.ac.kr.academy.service.log.LogHistoryService;
import com.ac.kr.academy.service.user.UserService;
import com.ac.kr.academy.util.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 전용 API
 * - 로그인 ID(username), 임시 비밀번호 생성
 * - 비밀번호 초기화
 */

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final UserService userService;
    private final LogHistoryService logHistoryService;

    //로그인 ID 자동생성
    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestParam String role,
                                           @RequestParam(required = false) Long deptId, //파라미터가 없어도 에러X
                                           @RequestParam String email,
                                           @RequestParam String name){
        try{
            Map<String, String> userInfo = userService.createUser(role, deptId, email, name);

            return ResponseEntity.ok("계정 생성 완료, 로그인 ID: " + userInfo.get("username")
                    + " / 임시 비밀번호: " + userInfo.get("password"));
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //비밀번호 초기화
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        if (username == null) {
            return ResponseEntity.badRequest().body("username 파라미터가 없습니다.");
        }

        User user = userService.findByUsername(username);
        String tempPassword = userService.resetPassword(username);
        return ResponseEntity.ok(Map.of(
                "message", "비밀번호가 초기화 되었습니다.",
                "tempPassword", tempPassword,
                "userId", user.getId()
        ));
    }


    //사용자 전체 조회
    @GetMapping("/user/all")
    public ResponseEntity<?> getAllUsers(){
        List<User> userList = userService.getAllUsers();
        if(userList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("등록된 사용자가 없습니다.");
        }
        return ResponseEntity.ok(userList);
    }

    //권한별 사용자 조회
    @GetMapping("/user/role")
    public ResponseEntity<?> getUsersByRole(@RequestParam String role){
        List<User> userList = userService.getUsersByRole(role);
        if(userList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 권한을 가진 사용자가 없습니다.");
        }
        return ResponseEntity.ok(userList);
    }

    //사용자 상세정보 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserDetail(@PathVariable Long userId){
        try{
            User user = userService.findById(userId);
            if(user == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }

            Object roleDetail = userService.findMyPageInfo(userId, user.getRole());
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("role", user.getRole());
            if(roleDetail != null) response.put("roleDetail", roleDetail);

            return ResponseEntity.ok(response);
        } catch (Exception e){
            log.error("사용자 상세 정보 조회중 오류 발생: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("사용자 상세 정보 조회중 오류 발생: " + e.getMessage());
        }
    }


    //사용자 정보 수정
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                        @RequestBody UpdateUserRequestDTO requestDTO){
        if(!userId.equals(requestDTO.getUser().getId())){
            return ResponseEntity.badRequest().body("ID가 일치하지 않습니다.");
        }
        userService.updateUser(requestDTO.getUser(), requestDTO.getRoleEntity());
        return ResponseEntity.ok("사용자 정보가 수정되었습니다.");
    }

    //사용자 삭제
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId){
        try{
            User user = userService.findById(userId);
            if(user == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }
            userService.deleteUser(userId);
            return ResponseEntity.ok("사용자 삭제를 완료했습니다.");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 삭제 중 오류가 발생했습니다. " + e.getMessage());
        }
    }

    //학생 상태값 변경 (재학중, 휴학중, 졸업)
    @PutMapping("/update-user/{userId}/status")
    public ResponseEntity<?> updateStudentStatus(@PathVariable Long userId, @RequestParam String status){
        userService.updateStudentStatus(userId, status);
        return ResponseEntity.ok("사용자 iD " + userId + "의 상태를 '" + status + "'로 변경 완료했습니다.");
    }


    //접속 기록 조회
    @GetMapping("/logs/history")
    public ResponseEntity<?> getLogsHistory(){
        List<LogHistory> logs = logHistoryService.getAllLogs();
        return ResponseEntity.ok(logs);
    }

    //로그 모니터링
    @GetMapping("/logs/monitor")
    public ResponseEntity<?> getLogs(@RequestParam(defaultValue = "100") int lines){
        try{
            List<String> logs = LogUtils.tail(lines);
            if(logs.isEmpty()){
                //JSON 응답을 위해 Map 사용
                Map<String, Object> response = Map.of(
                        "status", "success",
                        "message", "최근 " + lines +"줄 안에 WARN/ERROR 로그가 없습니다.",
                        "logs", List.of()
                );
                return ResponseEntity.ok(response);
            }
            //로그가 있을 때도 JSON 객체로 감싸서 반환
            Map<String, Object> response = Map.of(
                    "status", "success",
                    "logs", logs
            );
            return ResponseEntity.ok(response);
        } catch (Exception e){
            //오류 응답도 JSON으로
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("status", "error", "message", e.getMessage())
            );
        }
    }


    //교수 -> 지도교수 권한 변경
    @PostMapping("/assign-advisor")
    public ResponseEntity<?> assignAdvisorByDept(@RequestParam Long professorUserId, @RequestParam Long deptId){
        try{
            //서비스 호출, 결과 메세지 바로 받기
            String resultMessage = userService.assignAdvisorByDeptId(professorUserId, deptId);
            //성공 응답
            return ResponseEntity.ok(resultMessage);
        } catch (IllegalArgumentException e){
            //ID를 찾을 수 없을 때
            return ResponseEntity.badRequest().body("실패: " + e.getMessage());
        } catch (Exception e){
            //서버내부 오류
            log.error("교수ID: {}, 학과ID: {} 지정 중 오류 발생", professorUserId, deptId, e); //e: 예외의 전체 호출 스택(Stack Trace) 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("실패: 지도교수 지정 중 오류가 발생했습니다.");
        }
    }

    //지도교수 권한 회수 (지도교수 -> 교수)
    @PostMapping("/revert-advisor-role")
    public ResponseEntity<String> revertAdvisorRole(@RequestParam Long professorUserId){
        try{
            //서비스 호출
            String resultMessage = userService.revertAdvisorRole(professorUserId);
            return ResponseEntity.ok(resultMessage);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body("실패: " + e.getMessage());
        } catch (Exception e){
            log.error("지도교수 ID: {} 권한 해제 중 오류 발생", professorUserId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("실패: 지도교수 권한 해제 중 오류가 발생했습니다.");
        }
    }
}