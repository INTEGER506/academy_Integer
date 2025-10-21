package com.ac.kr.academy.service.notice;

import com.ac.kr.academy.domain.notice.Notice;

import java.util.List;

public interface NoticeService {
    //생성
    void createNotice(Notice notice);

    //전체 조회
    List<Notice> getNoticeList();

    //상세 조회
    Notice getNoticeDetail(Long id);

    //수정
    Notice editNotice(Notice notice);

    //삭제
    boolean removeNotice(Long id);

}
