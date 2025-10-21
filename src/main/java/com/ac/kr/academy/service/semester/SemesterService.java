package com.ac.kr.academy.service.semester;

import com.ac.kr.academy.domain.semester.Semester;
import java.util.List;
import java.util.Optional;

public interface SemesterService {

    /** 현재 수강신청 기간 기준으로 신청 가능한 학기 조회 */
    Optional<Semester> getCurrentSemester();

    /** 수강신청 기간 외에도 사용할 수 있는 최근 학기 조회 (시간표 등에서 사용) */
    Optional<Semester> getLatestSemester();

    /** 관리자 기능: 학기 등록 */
    void registerSemester(Semester semester);

    /** 관리자 기능: 학기 수정 */
    void updateSemester(Semester semester);

    /** 관리자 기능: 학기 삭제 */
    void deleteSemester(Long id);

    /** 관리자 기능: 모든 학기 조회 */
    List<Semester> findAllSemesters();

    /** 수강신청 가능 여부 판단 (현재 시간 기준) */
    boolean isEnrollmentPeriod(Semester semester);

    /** 수강신청 가능 여부 판단 (시간 테스트용) 관리자용 */
    boolean isEnrollmentPeriod(Semester semester, java.time.LocalDateTime currentTime);

    /** 현재 활성화된 수강신청 기간인지 전체적으로 판별 (현재 학기 기준) */
    boolean isEnrollmentPeriod();
}