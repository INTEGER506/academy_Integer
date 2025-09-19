package com.ac.kr.academy.service.grade;


import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.mapper.grade.GradeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;

    /*================================교수================================*/

    // 강의(Course)별 성적 목록
    @Override
    public List<Grade> findByCourseAll(Long professorId, Long courseId) {
        return
    }

    //성적 등록
    @Override
    public void addGrade(Grade grade, Long professorId) {
        gradeMapper.insert(grade);
    }

    //성적 수정
    @Override
    public void editGrade(Grade grade, Long professorId) {
        gradeMapper.update(grade);
    }

    //성적 삭제
    @Override
    public void deleteGrade(Long id, Long professorId) {
        gradeMapper.delete(id);
    }

    /*================================학생================================*/
    // 성적 목록
    @Override
    public List<Grade> findMyGrade(Long studentId, Long semesterId, Long subjectId) {
        return List.of();
    }

    // 단건 조회
    @Override
    public Grade findMyGradeById(Long id, Long studentId) {
        return gradeMapper.findMyGradeById(id, studentId);
    }


    /*================================공통================================*/
    // 성적 단건 조회
    @Override
    public Grade findGrade(Long id) {
        return gradeMapper.findById(id);
    }


}
