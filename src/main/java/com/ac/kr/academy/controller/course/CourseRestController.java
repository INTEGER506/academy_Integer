package com.ac.kr.academy.controller.course;

import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.security.CustomUserDetails;
import com.ac.kr.academy.service.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseRestController {

    private final CourseService courseService;

    /** 강의 개설 */
    @PreAuthorize("hasRole('PROFESSOR')")
    @PostMapping
    public ResponseEntity<String> addCourse(@RequestBody @Valid CourseCreateRequestDTO courseRequestDTO,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        courseService.addCourse(courseRequestDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("강의가 성공적으로 개설되었습니다.");
    }

    /** 강의 단건 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<CourseListResponseDTO> getCourseById(@PathVariable Long id) {
        CourseListResponseDTO course = courseService.findById(id);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(course);
    }

    /** 강의 수정 */
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateCourse(@PathVariable Long id,
                                               @RequestBody @Valid CourseUpdateRequestDTO courseRequestDTO,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        courseService.update(courseRequestDTO, id, userId);
        return ResponseEntity.ok("강의 정보가 수정되었습니다.");
    }

    /** 강의 삭제 */
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();

        try {
            courseService.delete(id, userId);
            return ResponseEntity.ok("강의가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("강의 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /** 강의 폐강 */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/close/{id}")
    public ResponseEntity<String> closeCourse(@PathVariable("id") Long id,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        try {
            courseService.closeCourse(id, userId);
            return ResponseEntity.ok("폐강이 완료되었습니다.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("폐강 권한이 없습니다.");
        } catch (IllegalStateException e) {
            // 수강신청 기간 중, 이미 폐강된 강의 등
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류로 인해 폐강 처리에 실패했습니다: " + e.getMessage());
        }
    }
}

