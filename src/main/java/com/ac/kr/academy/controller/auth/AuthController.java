package com.ac.kr.academy.controller.auth;

import com.ac.kr.academy.domain.dept.Dept;
import com.ac.kr.academy.domain.user.User;
import com.ac.kr.academy.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * JSP 페이지 이동 역할
 */

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    //기본 루트 접속시 로그인페이지로 이동
    @GetMapping("/")
    public String home(){
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()) {
            // 이미 로그인되어 있으면 홈으로 이동
            return "redirect:/auth/index";
        }
        return "auth/login";
    }

    //비밀번호 찾기
    @GetMapping("/find-password")
    public String findPasswordPage(){
        return "auth/find-password";
    }

    //비밀번호 변경 페이지로 이동
    @GetMapping("/change-password")
    public String changePasswordPage(){
        return "auth/change-password";
    }

    //비밀번호 초기화
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reset-password")
    public String resetPasswordPage(){
        return "auth/reset-password";
    }

    //접속기록관리
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/log-history")
    public String logHistoryPage(){
        return "log/log-history";
    }

    //로그 모니터링
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/log-monitor")
    public String logMonitorPage(){
        return "log/log-monitor";
    }

    //메인페이지
    @GetMapping("/index")
    public String indexPage(){
        return "common/index";
    }

    //교수 -> 지도교수 권한 변경
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/advisor-role")
    public String showAssignAdvisorPage(Model model){
        List<User> professors = userService.findAllProfessorsAndAdvisors();
        model.addAttribute("professorList", professors);

        List<Dept> depts = userService.findAllDepts();
        model.addAttribute("deptList", depts);

        return "admin/advisor-role";
    }
}