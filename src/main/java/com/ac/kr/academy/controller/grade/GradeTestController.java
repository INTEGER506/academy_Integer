//package com.ac.kr.academy.controller.grade;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequestMapping("/grade/test") // ← 테스트 전용 prefix
//public class GradeTestController {
//
//    // ── 학생 ───────────────────────────────────────────────
//    @GetMapping("/student/list")
//    public String studentList(
//            @RequestParam(required = false, defaultValue = "1") Long studentId,
//            @RequestParam(required = false) String type,
//            @RequestParam(required = false) String keyword,
//            Model model) {
//        model.addAttribute("studentId", studentId);
//        return "grade/student/list";
//    }
//
//    @GetMapping("/student/detail")
//    public String studentDetail(
//            @RequestParam(required = false, defaultValue = "1") Long studentId,
//            @RequestParam(required = false, defaultValue = "1") Long id,
//            Model model) {
//        model.addAttribute("studentId", studentId);
//        model.addAttribute("id", id);
//        return "grade/student/detail";
//    }
//
//    // ── 교수 ───────────────────────────────────────────────
//    @GetMapping("/professor/list")
//    public String professorList() { return "grade/professor/list"; }
//
//    @GetMapping("/professor/system-list")
//    public String systemList() { return "grade/professor/system-list"; }
//
//    // ── 관리자 ─────────────────────────────────────────────
//    @GetMapping("/admin/alphabet-list")
//    public String adminAlphaList() { return "grade/admin/alphabet-list"; }
//
//    @GetMapping("/admin/alphabet-form")
//    public String adminAlphaForm() { return "grade/admin/alphabet-form"; }
//}