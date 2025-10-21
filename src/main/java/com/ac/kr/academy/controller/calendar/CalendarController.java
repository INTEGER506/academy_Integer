package com.ac.kr.academy.controller.calendar;


import com.ac.kr.academy.domain.calendar.Calendar;
import com.ac.kr.academy.service.calendar.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    // 1. 학사일정 '조회' 페이지
    @GetMapping({"","/list"})
    public String calendar(Model model) {
        List<Calendar> calendars = calendarService.findAll();
        model.addAttribute("calendars", calendars);
        return "calendar/list";
    }

    // 2. 일정 추가 페이지
    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public String addForm() {
        return "calendar/add";
    }

    // 3. 일정 수정 페이지
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Calendar calendar = calendarService.findById(id);
        model.addAttribute("calendar", calendar);
        return "calendar/edit";
    }
}
