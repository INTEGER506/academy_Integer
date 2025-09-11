package com.ac.kr.academy.service.user;

import com.ac.kr.academy.domain.user.*;
import com.ac.kr.academy.mapper.user.UserMapper;
import com.ac.kr.academy.mapper.user.advisor.AdvisorMapper;
import com.ac.kr.academy.mapper.user.professor.ProfessorMapper;
import com.ac.kr.academy.mapper.user.staff.StaffMapper;
import com.ac.kr.academy.mapper.user.student.StudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final ProfessorMapper professorMapper;
    private final StaffMapper staffMapper;
    private final AdvisorMapper advisorMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(User user, Object roleEntity){
        //공통 계정(users 테이블) 등록
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordTemp(true);
        userMapper.insertUser(user);

        //역할에 따라 상세정보 각 테이블 등록
        if("ROLE_STUDENT".equals(user.getRole())){
            Student student = (Student) roleEntity;
            student.setUserId(user.getId());
            student.setStudentNum(user.getUsername());
            studentMapper.insertStudent(student);
        } else if ("ROLE_PROFESSOR".equals(user.getRole())) {
            Professor professor = (Professor) roleEntity;
            professor.setUserId(user.getId());
            professor.setProfessorNum(user.getUsername());
            professorMapper.insertProfessor(professor);
        } else if ("ROLE_STAFF".equals(user.getRole())) {
            Staff staff = (Staff) roleEntity;
            staff.setUserId(user.getId());
            staff.setStaffNum(user.getUsername());
            staffMapper.insertStaff(staff);
        }
    }

    @Transactional
    public void setAdvisor(Long professorId, Long studentId){
        advisorMapper.insertAdvisor(professorId, studentId);
    }

    @Transactional
    public void updateUser(User user, Object roleEntity){
        userMapper.updateUser(user);

        if("ROLE_STUDENT".equals(user.getRole())){
            studentMapper.updateStudent((Student) roleEntity);
        } else if ("ROLE_PROFESSOR".equals(user.getRole())) {
            professorMapper.updateProfessor((Professor) roleEntity);
        } else if ("ROLE_STAFF".equals(user.getRole())) {
            staffMapper.updateStaff((Staff) roleEntity);
        }
    }

    @Transactional
    public void deleteUser(User user){
        if("ROLE_STUDENT".equals(user.getRole())){
            studentMapper.deleteStudent(user.getId());
        } else if ("ROLE_PROFESSOR".equals(user.getRole())) {
            professorMapper.deleteProfessor(user.getId());
        } else if ("ROLE_STAFF".equals(user.getRole())) {
            staffMapper.deleteStaff(user.getId());
        }
        userMapper.deleteUser(user.getId());
    }

    public User findByUsername(String username){
        return userMapper.findByUsername(username);
    }

    public User findById(Long id){
        return userMapper.findById(id);
    }

    //비밀번호 변경 - 사용자
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword){
        User user = userMapper.findByUsername(username);
        if(user == null){
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        if(!passwordEncoder.matches(currentPassword, user.getPassword())){
            throw new IllegalArgumentException("입력하신 비밀번호가 일치하지 않습니다.");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        userMapper.updateUserPassword(username, encodedNewPassword, false);
    }

    //비밀번호 초기화 - 관리자
    @Transactional
    public String resetPassword(String username){
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        userMapper.updateUserPassword(username, passwordEncoder.encode(tempPassword), true);
        return tempPassword;
    }
}
