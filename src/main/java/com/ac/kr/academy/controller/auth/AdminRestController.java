package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.domain.user.Student;
import com.ac.kr.academy.domain.user.User;
import com.ac.kr.academy.mapper.admin.AdminMapper;
import com.ac.kr.academy.mapper.user.UserMapper;
import com.ac.kr.academy.mapper.user.student.StudentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.UUID;

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

    private final UserMapper userMapper;
    private final AdminMapper adminMapper;
    private final StudentMapper studentMapper;
    private final BCryptPasswordEncoder passwordEncoder;


    @PreAuthorize("hasRole('ADMIN')")   //관리자만 접근가능
    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestParam String role,
                                           @RequestParam(required = false) Long deptId, //파라미터가 없어도 에러X
                                           @RequestParam String email,
                                           @RequestParam String name){
        //역할에 따라 순번 키와 최종 사용자 ID(username)을 생성
        String sequenceKey;
        String username;

        //임시 비밀번호 생성
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        String encodedPassword = passwordEncoder.encode(tempPassword);

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setRole(role);
        user.setPassword(encodedPassword);
        user.setPasswordTemp(true);
        userMapper.insertUser(user);

        if("ROLE_STUDENT".equals(role)){    //학번 = 연도/학과/순번
            String formatedDeptId = String.format("%02d", deptId);
            sequenceKey = Year.now().toString() + formatedDeptId;
            adminMapper.incrementSequence(sequenceKey);
            int sequence = adminMapper.findSequenceNum(sequenceKey);
            username = sequenceKey + String.format("%03d", sequence);

            user.setUsername(username);
            userMapper.updateUsername(user.getId(), username);

            //Student 테이블에 데이터 삽입
            Student student = new Student();
            student.setStudentNum(username);
            student.setUserId(user.getId());
            student.setDeptId(deptId);
            studentMapper.insertStudent(student);

        }else{          //나머지 사용자 로그인 ID는 역할명(예: PROF)을 순번키로 사용
            sequenceKey = role.replace("ROLE_", "");
            adminMapper.incrementSequence(sequenceKey);
            int sequence = adminMapper.findSequenceNum(sequenceKey);
            username = sequenceKey + String.format("%05d", sequence);

            user.setUsername(username);
            userMapper.updateUsername(user.getId(), username);

        }

        return ResponseEntity.ok("계정 생성 완료. 로그인 ID: " + username);
    }

    //비밀번호 초기화
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String username){
        //사용자 존재여부 확인
        User user = userMapper.findByUsername(username);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        //새 임시 비밀번호 생성
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        String encodedPassword = passwordEncoder.encode(tempPassword);

        userMapper.updateUserPassword(username, encodedPassword, true);

        log.info("사용자 {}의 비밀번호 초기화 완료", username);
        return ResponseEntity.ok(username + " 의 비밀번호가 초기화 되었습니다. 새 임시 비밀번호: " + tempPassword);
    }
}
