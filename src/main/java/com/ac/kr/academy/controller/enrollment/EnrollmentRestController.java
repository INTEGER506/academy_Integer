package com.ac.kr.academy.controller.enrollment;

import com.ac.kr.academy.dto.enrollment.EnrollmentListResponseDTO;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.security.CustomUserDetails;
import com.ac.kr.academy.service.enrollment.EnrollmentService;
import com.ac.kr.academy.service.semester.SemesterService;
import com.ac.kr.academy.service.user.StudentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class EnrollmentRestController {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final SemesterService semesterService;

    // 수강 신청 가능 목록 조회
    @GetMapping({"","/list"})
    public ResponseEntity<PageResponseDTO<EnrollmentListResponseDTO>> availableCoursesData(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .searchType(searchType)
                .searchKeyword(searchKeyword)
                .build();

        // 2. null 체크 후 DTO의 int 필드에 안전하게 값 설정
        //    (page="" -> null 처리 -> DTO의 기본값 1 유지)
        if (page != null) {
            pageRequestDTO.setPage(page);
        }
        if (pageSize != null) {
            pageRequestDTO.setPageSize(pageSize);
        }

        Long userId = userDetails.getUserId();
        Long studentId = studentService.findStudentIdByUserId(userId);

        // 현재 학기 ID 조회
        Long currentSemesterId = semesterService.getCurrentSemester()
                .orElseThrow(() -> new IllegalStateException("현재 수강신청 학기가 설정되어 있지 않아 강의 목록을 불러올 수 없습니다."))
                .getId();

        // 강의 목록 데이터 조회
        PageResponseDTO<EnrollmentListResponseDTO> courseResponseDTO =
                enrollmentService.findAvailableCoursesPaged(pageRequestDTO, studentId, currentSemesterId);


        return ResponseEntity.ok(courseResponseDTO);
    }

    // 학점 현황 조회
    @GetMapping("/credits")
    public ResponseEntity<Map<String, Object>> getCreditStatus(@AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();
        Long studentId = studentService.findStudentIdByUserId(userId);

        // 현재 신청 학점 조회
        int currentCredits = enrollmentService.findTotalCreditsByStudentId(studentId);

        // 응답 데이터 구조화
        Map<String, Object> response = new HashMap<>();
        response.put("currentCredits", currentCredits); // 현재 신청 학점
        response.put("maxCredits", 18); // 최대 신청 학점 (고정 값)

        return ResponseEntity.ok(response);
    }


    // ✅ 수강 신청 처리
    @PostMapping("/{courseId}")
    public ResponseEntity<String> enrollCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();
        Long studentId = studentService.findStudentIdByUserId(userId);

        try {
            enrollmentService.enroll(courseId, studentId);
            return ResponseEntity.status(HttpStatus.CREATED).body("수강 신청이 완료되었습니다.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // 수강 취소 처리
    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> cancelEnrollment(@PathVariable Long courseId,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();
        Long studentId = studentService.findStudentIdByUserId(userId);

        try {
            enrollmentService.cancel(courseId, studentId);
            return ResponseEntity.ok("수강 취소가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}