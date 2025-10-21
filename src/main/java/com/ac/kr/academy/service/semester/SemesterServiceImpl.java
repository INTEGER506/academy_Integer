package com.ac.kr.academy.service.semester;

import com.ac.kr.academy.domain.semester.Semester;
import com.ac.kr.academy.mapper.semester.SemesterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SemesterServiceImpl implements SemesterService {

    private final SemesterMapper semesterMapper;

    // 현재 수강신청 기간 기준으로 학기 조회
    @Override
    public Optional<Semester> getCurrentSemester() {
        return semesterMapper.findCurrentSemester();
    }

    // 가장 최근 학기 조회 (수강신청 기간 외 조회용)
    @Override
    public Optional<Semester> getLatestSemester() {
        return semesterMapper.findLatestSemester();
    }

    //  관리자 CRUD 기능
    @Override
    @Transactional
    public void registerSemester(Semester semester) {
        semesterMapper.insert(semester);
    }

    @Override
    @Transactional
    public void updateSemester(Semester semester) {
        semesterMapper.update(semester);
    }

    @Override
    @Transactional
    public void deleteSemester(Long id) {
        semesterMapper.deleteById(id);
    }

    @Override
    public List<Semester> findAllSemesters() {
        return semesterMapper.findAll();
    }

    // 현재 수강신청 기간 여부 (자동 판별)
    @Override
    public boolean isEnrollmentPeriod() {
        return getCurrentSemester()
                .map(semester -> isEnrollmentPeriod(semester, LocalDateTime.now()))
                .orElse(false);
    }

    // 수강신청 가능 여부 판단
    @Override
    public boolean isEnrollmentPeriod(Semester semester) {
        return isEnrollmentPeriod(semester, LocalDateTime.now());
    }

    @Override
    public boolean isEnrollmentPeriod(Semester semester, LocalDateTime currentTime) {
        if (semester == null || semester.getEnrollmentStartDate() == null || semester.getEnrollmentEndDate() == null) {
            return false;
        }
        LocalDateTime start = semester.getEnrollmentStartDate();
        LocalDateTime end = semester.getEnrollmentEndDate();
        return !currentTime.isBefore(start) && !currentTime.isAfter(end);
    }
}