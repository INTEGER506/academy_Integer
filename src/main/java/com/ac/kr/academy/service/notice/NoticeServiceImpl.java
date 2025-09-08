package com.ac.kr.academy.service.notice;

import com.ac.kr.academy.domain.notice.Notice;
import com.ac.kr.academy.mapper.notice.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{
    private final NoticeMapper noticeMapper;

    //공지 생성 -> 알림 발송
    @Override
    public void createNotice(Notice notice) {
        noticeMapper.insertNotice(notice);  //DB에 저장

        //긴급(중요)일때 알림 생성 / 하고 발송? 일단 알림 만들고 다시 ===================
        if(notice.getIsUrgent()==1){
            //알림 서비스 호출(알림 생성 메소드)
        }
    }

    //전체 조회
    @Override
    public List<Notice> getNoticeList() {
        return noticeMapper.getAllNotices();
    }

    //상세조회
    @Override
    public Notice getNoticeDetail(Long id) {
        //일단 조회수를 올려 / *순서 중요
        noticeMapper.increaseViewCount(id);

        //상세정보 가져와
        return noticeMapper.findByNoticeId(id);
    }

    //수정
    @Override
    public void editNotece(Notice notice) {
        noticeMapper.updateNotice(notice);
    }

    //삭제
    @Override
    public void removeNotice(Long id) {
        noticeMapper.deleteNotice(id);
    }
}
