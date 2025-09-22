package com.ac.kr.academy.service.grade;


import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.mapper.grade.GradeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;

    /*================================관리자================================*/

    /*================================교수================================*/
    // 수업별 성적 목록
    @Override
    public PageResponseDTO<Grade> listByCourse(Long courseId, PageRequestDTO request, Long professorId) {
        int page = request.getPage() <= 0 ? 1 : request.getPage();
        int size = request.getPageSize() <= 0 ? 10 : request.getPageSize();

        long total = gradeMapper.countMyGrade(courseId, professorId);
        List<Grade> items = gradeMapper.findByCourse(courseId, professorId);

        return new PageResponseDTO<>(items, (int) total, page, size);
    }

    // 성적 등록
    @Override
    public void addGrade(Grade grade, Long professorId) {
        gradeMapper.insert(grade);
    }

    // 성적 수정
    @Override
    public void editGrade(Grade grade, Long professorId) {
        gradeMapper.update(grade);
    }

    @Override
    public void deleteGrade(Long gradeId, Long professorId) {
        gradeMapper.delete(gradeId);
    }

    /*================================학생================================*/
    @Override
    public Grade findGrade(Long id) {
        return null;
    }

    @Override
    public Grade getMyGrade(Long studentId, Long id) {
        return null;
    }

    @Override
    public PageResponseDTO<Grade> listMyGrades(Long studentId, PageRequestDTO pageRequestDTO) {
        return null;
    }

    @Override
    public void deleteGrade(Long gradeId, Long professorId) {

    }

    @Override
    public void editGrade(Grade grade, Long professorId) {

    }

    @Override
    public void addGrade(Grade grade, Long professorId) {

    }

    @Override
    public void setGradeSystem(Long courseId, Long professorId) {

    }

    @Override
    public GradeSystem getGradeSystem(Long courseId, Long professorId) {
        return null;
    }

    @Override
    public void deleteAlphabetRule(Long id) {

    }

    @Override
    public void updateAlphabetRule(AlphabetSystem rule) {

    }

    @Override
    public void addAlphabetRule(AlphabetSystem rule, boolean global) {

    }

    @Override
    public List<AlphabetSystem> getAlphabetBySubject(Long subjectId) {
        return List.of();
    }

    @Override
    public List<AlphabetSystem> getAlphabetGlobal() {
        return List.of();
    }
}
