package com.ac.kr.academy.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")  // JSP 전용
public class UserPageController {
    
    @GetMapping("/user-create")
    public String userCreateForm() {
        return "admin/user/user-create";
    }

    @GetMapping("/user-list")
    public String userList() {
        return "admin/user/user-list";
    }

    @GetMapping("/user-detail")
    public String userDetail(@RequestParam("id") Long id, Model model) {
        model.addAttribute("userId", id);
        return "admin/user/user-detail";
    }

    @GetMapping("/user-update")
    public String userUpdate(@RequestParam("id") Long id, Model model) {
        model.addAttribute("userId", id);
        return "admin/user/user-update";
    }

    @GetMapping("/user-reset")
    public String userReset(@RequestParam("id") Long id, Model model) {
        model.addAttribute("userId", id);
        return "admin/user/user-reset";
    }

    @GetMapping("/user-delete")
    public String userDelete(@RequestParam("id") Long id, Model model) {
        model.addAttribute("userId", id);
        return "admin/user/user-delete";
    }
}
