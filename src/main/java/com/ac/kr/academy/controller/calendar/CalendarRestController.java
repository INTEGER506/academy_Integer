package com.ac.kr.academy.controller.calendar;


import com.ac.kr.academy.domain.calendar.Calendar;
import com.ac.kr.academy.security.CustomUserDetails;
import com.ac.kr.academy.service.calendar.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendar")
public class CalendarRestController {

    private final CalendarService calendarService;

    // 1. 일정 작성 (생성) 처리
    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<String> saveCalendar(
            @RequestBody Calendar calendar,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 1. color 기본값 설정 (null 체크)
        if (calendar.getColor() == null || calendar.getColor().trim().isEmpty()) {
            calendar.setColor("#3366ff");
        }

        // 2. userId 설정 (교직원 ID를 인증 정보에서 가져와 설정)
        Long staffId = userDetails.getUserId();
        calendar.setUserId(staffId);

        log.info("REST API Request body for save: {}", calendar);
        calendarService.saveCalendar(calendar);

        return ResponseEntity.status(HttpStatus.CREATED).body("일정이 성공적으로 저장되었습니다.");
    }

    // 2. 일정 수정 처리
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<String> updateCalendar(@PathVariable Long id, @RequestBody Calendar calendar) {
        log.info("REST API Request body for update: {}", calendar);


        calendar.setId(id);
        calendarService.saveCalendar(calendar);

        return ResponseEntity.ok("일정이 성공적으로 수정되었습니다.");
    }

    // 3. 일정 삭제 처리
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<String> deleteCalendar(@PathVariable Long id) {
        calendarService.delete(id);

        // HTTP 200 OK 응답
        return ResponseEntity.ok("일정이 성공적으로 삭제되었습니다.");
    }

    // 전체 일정
    @GetMapping("/data")
    public ResponseEntity<List<Calendar>> getCalendarData() {
        List<Calendar> calendars = calendarService.findAll();
        return ResponseEntity.ok(calendars);
    }
}
