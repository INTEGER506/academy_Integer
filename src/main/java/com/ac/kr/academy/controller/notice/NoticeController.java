package com.ac.kr.academy.controller.notice;

import com.ac.kr.academy.domain.notice.Notice;
import com.ac.kr.academy.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {
    private final NoticeService noticeService;

    //jsp 만들기 전 / 임시로 이름지음

    //전체 조회
    @GetMapping("/list")
    public String getNoticeList(Model model){
        List<Notice> noticeList = noticeService.getNoticeList();
        model.addAttribute("noticeList", noticeList);
        return "notice/list";   //notice/list.jsp
    }

    //왜 Long id가 아니라 @RequestParam("id") Long id ?
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




}
