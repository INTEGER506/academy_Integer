package com.ac.kr.academy.mapper.user.advisor;

import com.ac.kr.academy.domain.user.Advisor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 학생 <-> 교수 1:1 매핑 / 매핑 관리
 * 특정 교수 -> 특정 학생의 졸업 심사 권한을 가짐
 * */

@Mapper
public interface AdvisorMapper {
    //지도교수 지정
    void insertAdvisor(@Param("professorId") Long professorId, @Param("studentId") Long studentId);
    int updateAdvisor(Advisor advisor);     //지도교수 교체
    int deleteAdvisor(Long id);             //매핑 해제(관리자 권한만 가능)

    List<Advisor> findAllAdvisor();         //관리자 권한만 가능

    //특정 교수의 제자 조회
    List<Advisor> findByProfessorId(@Param("professorId") Long professorId);

    //특정 학생의 지도교수 조회
    Advisor findByStudentId(@Param("studentId") Long studentId);
}
