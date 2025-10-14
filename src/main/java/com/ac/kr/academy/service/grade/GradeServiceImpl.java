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


import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;


    /*================================관리자================================*/

    // 전체 규정 목록
    @Override
    public PageResponseDTO<AlphabetSystem> listAlphabetGlobal(String searchType,
                                                              String searchKeyword,
                                                              PageRequestDTO req) {
        long total = gradeMapper.countAlphabetGlobal();
        List<AlphabetSystem> rows = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findAlphabetGlobal(req.getStart(), req.getEnd());
        return PageResponseDTO.pageOf(rows, total, req);
    }

    // 규정 추가
    @Override
    @Transactional
    public void addAlphabetGlobal(AlphabetSystem rule) {
        gradeMapper.insertAlphabetGlobal(rule);
    }

    // 규정 수정
    @Override
    @Transactional
    public void updateAlphabetRule(AlphabetSystem rule) {
        gradeMapper.updateAlphabetRule(rule);
    }

    // 규정 삭제
    @Override
    @Transactional
    public void deleteAlphabetRule(Long id) {
        gradeMapper.deleteAlphabetRule(id);
    }

    // 과목/수강 규정 목록
    @Override
    public PageResponseDTO<AlphabetSystem> listAlphabetBySubject(Long subjectId,
                                                                 Long enrollmentId,
                                                                 String searchType,
                                                                 String searchKeyword,
                                                                 PageRequestDTO req) {
        long total = gradeMapper.countAlphabetSubject(subjectId, enrollmentId);
        List<AlphabetSystem> rows = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findAlphabetBySubject(
                                                    subjectId,
                                                    enrollmentId,
                                                    req.getStart(),
                                                    req.getEnd()
                                                    );
        return PageResponseDTO.pageOf(rows, total, req);
    }

    /*================================교수================================*/

    // 교수 성적 목록
    @Override
    public PageResponseDTO<Grade> listByCourse(Long professorId,
                                               Long courseId,
                                               Long subjectId,
                                               String searchType,
                                               String searchKeyword,
                                               PageRequestDTO req) {

        long total = gradeMapper.countByCourse(professorId, courseId, subjectId, searchType, searchKeyword);

        List<Grade> list = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findByCourse(
                professorId, courseId, subjectId,
                searchType, searchKeyword,
                req.getStart(), req.getEnd(), req.getPageSize()
        );

        return PageResponseDTO.pageOf(list, total, req);
    }

    // 성적 등록
    @Override
    @Transactional
    public void addGrade(Grade grade, Long professorId) {
        gradeMapper.insert(grade, professorId);
    }

    // 성적 수정
    @Override
    @Transactional
    public void editGrade(Grade grade, Long professorId) {
        gradeMapper.update(grade, professorId);
    }

    // 성적 삭제
    @Override
    @Transactional
    public void deleteGrade(Long gradeId, Long professorId) {
        gradeMapper.delete(gradeId, professorId);
    }

    // 점수 분배
    @Override
    public PageResponseDTO<GradeSystem> listGradeSystemByCourse(Long courseId,
                                                                String searchType,
                                                                String searchKeyword,
                                                                PageRequestDTO req) {

        long total = gradeMapper.countGradeSystemByCourse(courseId);
        List<GradeSystem> list = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findGradeSystemByCourse(
                courseId, req.getStart(), req.getEnd()
        );

        return PageResponseDTO.pageOf(list, total, req);
    }

    // 점수 분배 등록
    @Override
    @Transactional
    public void addGradeSystem(GradeSystem system, Long professorId) {
        gradeMapper.insertGradeSystem(system);
    }

    // 점수 분배 수정
    @Override
    @Transactional
    public void editGradeSystem(GradeSystem system, Long professorId) {
        gradeMapper.updateGradeSystem(system);
    }
    /*================================학생================================*/
    // 학생 성적 조회
    @Override
    public PageResponseDTO<Grade> listMyGrades(Long studentId,
                                               String searchType,
                                               String searchKeyword,
                                               PageRequestDTO req) {


        long total = gradeMapper.countMyGrade(studentId, searchType, searchKeyword);

        List<Grade> list = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findMyGrade(
                studentId, searchType, searchKeyword, req.getStart(),  req.getEnd()
        );

        return PageResponseDTO.pageOf(list, total, req);
    }

    // 성적 단건 조회
    @Override
    public Grade getMyGrade(Long studentId, Long id) {
        return (Grade) gradeMapper.findMyGradeById(studentId, id);
    }

    /*    =========================================================================================================================*/
    @Override
    public Grade findGrade(Long id) {
        return gradeMapper.findById(id);
    }


    @Override
    public void addAlphabetRule(AlphabetSystem rule, boolean global) {

    }

}
