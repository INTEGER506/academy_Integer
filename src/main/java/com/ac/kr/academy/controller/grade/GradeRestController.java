package com.ac.kr.academy.controller.grade;

import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.service.grade.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/grade")
public class GradeRestController {

    private final GradeService gradeService;

    // 글로벌 규정 전체 조회 (페이징 메서드를 큰 페이지로 감싸서 사용)
    @GetMapping("/alphabet/global")
    public ResponseEntity<List<AlphabetSystem>> getAlphabetGlobalAll(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword) {

        PageRequestDTO req = new PageRequestDTO();
        req.setPage(1);
        req.setPageSize(1000); // 충분히 큰 값

        PageResponseDTO<AlphabetSystem> page =
                gradeService.listAlphabetGlobal(searchType, searchKeyword, req);

        return ResponseEntity.ok(page.getData());
    }

    // 과목/수강 전용 규정 전체 조회
    @GetMapping("/alphabet/subject")
    public ResponseEntity<List<AlphabetSystem>> getAlphabetBySubjectAll(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long enrollmentId,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword) {

        PageRequestDTO req = new PageRequestDTO();
        req.setPage(1);
        req.setPageSize(1000);

        PageResponseDTO<AlphabetSystem> page =
                gradeService.listAlphabetBySubject(subjectId, enrollmentId, searchType, searchKeyword, req);

        return ResponseEntity.ok(page.getData());
    }

}
