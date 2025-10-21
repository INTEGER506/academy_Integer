package com.ac.kr.academy.mapper.semester;

import com.ac.kr.academy.domain.semester.Semester;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SemesterMapper {

    /** 새로운 학기 추가 */
    void insert(Semester semester);

    /** 학기 ID로 상세 정보를 조회 */
    Optional<Semester> findById(Long id);

    /** 모든 학기 조회 */
    List<Semester> findAll();

    /** 학기 정보 수정 */
    void update(Semester semester);

    /** 학기 삭제 */
    void deleteById(Long id);

    /**
     * 현재 날짜 기준으로 수강신청 가능한 학기를 조회
     * enrollmentStartDate <= SYSDATE <= enrollmentEndDate
     */
    Optional<Semester> findCurrentSemester();

    /** 가장 최근 학기 조회 (수강신청 기간 종료 후에도 사용 가능) */
    Optional<Semester> findLatestSemester();



}