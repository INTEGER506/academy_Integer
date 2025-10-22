package com.ac.kr.academy.mapper.user.student;

import com.ac.kr.academy.domain.user.Student;
import com.ac.kr.academy.dto.user.MyPageInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface StudentMapper {

    void insertStudent(Student student);
    int updateStudent(Student student); //마이페이지, 관리자 페이지
    int deleteStudent(@Param("id") Long userId);

    Student findByUserId(@Param("userId") Long userId);

    //학생 상태값 변경 (재학중, 휴학중, 졸업)
    int updateStatusByUserId(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 학생의 현재 재학 상태(예: '재학', '휴학', '제적')를 조회합니다.
     */
    String findStudentStatusByUserId(@Param("userId") Long userId);

    //userId로 학생 테이블의 고유 id 조회
    Long findStudentIdByUserId(@Param("userId") Long userId);

    //학과 ID 기반으로 해당 학과 학생들의 student 테이블 id 리스트 조회 (지도교수 관련 메소드)
    List<Long> findAllStudentIdByDeptId(@Param("deptId") Long deptId);

    // 학점 반환을 위한 신규 메서드 추가
    /**
     * 학생의 '현재 신청 학점'을 변경합니다.
     * 폐강 시에는 해당 학점만큼 '감소' 시킵니다. (creditChange가 음수가 됨)
     */
    int updateStudentEnrolledCredits(
            @Param("studentId") Long userId,
            @Param("creditChange") int creditChange // 변경할 학점 값 (예: -3)
    );

    //마이페이지용
    MyPageInfoDTO findMyPageInfoByUserId(@Param("userId") Long userId);
}