package com.ac.kr.academy.controller.notice;

import com.ac.kr.academy.domain.notice.Notice;
import com.ac.kr.academy.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {
    private final NoticeService noticeService;

    // 전체 조회
    @GetMapping({"", "/"})
    public String getNoticeList(Model model){
        List<Notice> noticeList = noticeService.getNoticeList();
        model.addAttribute("noticeList", noticeList);
        return "notice/list";
    }

    // 상세 조회
    @GetMapping("/detail/{id}")
    public String getNoticeDetail(@PathVariable Long id, Model model){
        Notice notice = noticeService.getNoticeDetail(id);
        model.addAttribute("notice", notice);
        return "notice/detail";
    }

    // 등록 폼
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String addForm(Model model){
        model.addAttribute("notice", new Notice());
        return "notice/add";
    }

    // 등록 처리
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String addNotice(@ModelAttribute Notice notice){
        noticeService.createNotice(notice);
        return "redirect:/notices/";
    }

    // 수정 폼
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model){
        Notice notice = noticeService.getNoticeDetail(id);
        model.addAttribute("notice", notice);
        return "notice/edit";
    }

    // 수정 처리
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit")
    public String editNotice(@ModelAttribute Notice notice){
        noticeService.editNotice(notice);
        return "redirect:/notices/detail/" + notice.getId();
    }

    // 삭제 처리
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public String removeNotice(@RequestParam("id") Long id){
        noticeService.removeNotice(id);
        return "redirect:/notices/";
    }
}
