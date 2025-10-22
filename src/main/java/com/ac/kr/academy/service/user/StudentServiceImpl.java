package com.ac.kr.academy.service.user;


import com.ac.kr.academy.domain.user.Student;
import com.ac.kr.academy.mapper.user.student.StudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;

    @Override
    public Long findStudentIdByUserId(Long userId) {
        Student student = studentMapper.findByUserId(userId);

        if (student == null) {
            throw new IllegalArgumentException("USER ID " + userId + "에 해당하는 학생 정보를 찾을 수 없습니다.");
        }
        return student.getId();
    }

    @Override
    @Transactional
    public void returnCredit(Long studentId, int credit) {
        // 학생의 '현재 신청 학점'을 폐강된 학점만큼 감소
        // credit은 양수이므로, 감소시키기 위해 -credit으로 전달
        studentMapper.updateStudentEnrolledCredits(studentId, -credit);
    }

    @Override
    @Transactional
    public String findStudentStatus(Long userId) {

        String studentStatus = studentMapper.findStudentStatusByUserId(userId);

        if (studentStatus == null) {
            throw new IllegalArgumentException("학생 ID: " + userId + "에 해당하는 학생 정보를 찾을 수 없습니다.");
        }
        return studentStatus;
    }

    @Override
    public void updateCurrentCredits(Long studentId, int change) {
        studentMapper.updateStudentEnrolledCredits(studentId, change);
    }
}