package com.ac.kr.academy.controller.grade;


import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.service.grade.GradeService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/grade")
public class GradeRestController {

    private final GradeService gradeService;

    /*================================교수================================*/
    // 교수 : 성적 목록
    @GetMapping("/professor/list")
    public ResponseEntity<PageResponseDTO<Grade>> listForProfessor(@RequestParam Long professorId,
                                                                   @RequestParam(required = false) Long courseId,
                                                                   @RequestParam(required = false) Long subjectId,
                                                                   @RequestParam(required = false) String searchType,
                                                                   @RequestParam(required = false) String searchKeyword,
                                                                   PageRequestDTO req){
        return ResponseEntity.ok(
                gradeService.listByCourse(professorId, courseId, subjectId, searchType, searchKeyword, req)
        );
    }

    // 점수 분배 목록
    @GetMapping("/api/grade/system")
    public ResponseEntity<PageResponseDTO<GradeSystem>> listGradeSystemByCourse(
            @RequestParam Long courseId,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword,
            PageRequestDTO req) {

        return ResponseEntity.ok(
                gradeService.listGradeSystemByCourse(courseId, searchType, searchKeyword, req)
        );
    }
    /*================================학생================================*/
    // 학생 성적 목록
    @GetMapping("/student/list")
    public ResponseEntity<PageResponseDTO<Grade>> listForStudent(@RequestParam Long studentId,
                                                                 @RequestParam(required = false) String searchType,
                                                                 @RequestParam(required = false) String searchKeyword,
                                                                 PageRequestDTO req){
        return ResponseEntity.ok(
                gradeService.listMyGrades(studentId, searchType, searchKeyword, req)
        );
    }
}
