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

    // 코스별 점수 가중치 단건 조회 (없으면 204)
    @GetMapping("/system")
    public ResponseEntity<GradeSystem> getGradeSystem(@RequestParam Long courseId) {
        GradeSystem gs = gradeService.getGradeSystemByCourse(courseId);
        if (gs == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(gs);
    }

    // 성적 미리보기 계산 (저장하지 않음)
    @PostMapping("/preview")
    public ResponseEntity<Map<String, Object>> preview(@RequestBody Grade grade) {
        Map<String, Object> result = gradeService.calculateGradePreview(grade);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 학생 집계: 총 취득학점, 평균 GPA
    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<Map<String, Object>> summarize(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.summarizeForStudent(studentId));
    }

    // 졸업요건 체크 (요건 파라미터 전달)
    @GetMapping("/student/{studentId}/graduation")
    public ResponseEntity<Map<String, Object>> graduation(
            @PathVariable Long studentId,
            @RequestParam long requiredScore,
            @RequestParam double requiredAvgGpa) {
        return ResponseEntity.ok(gradeService.checkGraduation(studentId, requiredScore, requiredAvgGpa));
    }
}
