package com.ac.kr.academy.controller.semester;

import com.ac.kr.academy.domain.semester.Semester;
import com.ac.kr.academy.service.semester.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semester")
@RequiredArgsConstructor
public class SemesterRestController {

    private final SemesterService semesterService;

    /** 현재 수강신청 가능 학기 조회 */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSemester() {
        return semesterService.getCurrentSemester()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body("현재 수강신청 가능한 학기가 없습니다."));
    }
}
