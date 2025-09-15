package com.ac.kr.academy.controller.notice;

import com.ac.kr.academy.domain.notice.Notice;
import com.ac.kr.academy.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * jsp 만들기 전 / 임시로 이름 지음
 * */
@Controller
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {
    private final NoticeService noticeService;

    //전체 조회
    @GetMapping("/list")
    public String getNoticeList(Model model){
        List<Notice> noticeList = noticeService.getNoticeList();
        model.addAttribute("noticeList", noticeList);
        return "notice/list";   //notice/list.jsp
    }

    //상세 조회
    @GetMapping("/detail")
    public String getNoticeDetail(@RequestParam("id") Long id, Model model){
        Notice notice = noticeService.getNoticeDetail(id);
        model.addAttribute("notice", notice);
        return "notice/detail";
    }

    //생성 폼으로 이동
    @GetMapping("/create")
    public String createForm(){
        return "notice/create";
    }

    //생성 처리
    @PostMapping("/create")
    public String createNotice(@ModelAttribute Notice notice){
        noticeService.createNotice(notice);
        return "redirect:/notices/list";
    }

    //수정 폼
    @GetMapping("/update")
    public String editForm(@RequestParam("id") Long id, Model model){
        Notice notice = noticeService.getNoticeDetail(id);
        model.addAttribute("notice", notice);
        return "notice/update";
    }

    //수정 처리
    @PostMapping("/update")
    public String editNotice(@ModelAttribute Notice notice){
        noticeService.editNotice(notice);
        return "redirect:/notices/detail?id=" + notice.getId();
    }

    //삭제
    @PostMapping("/delete")
    public String removeNotice(@RequestParam("id") Long id){
        noticeService.removeNotice(id);
        return "redirect:/notices/list";
    }

}
