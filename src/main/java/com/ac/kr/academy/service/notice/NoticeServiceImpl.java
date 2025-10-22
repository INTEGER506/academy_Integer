package com.ac.kr.academy.service.notice;

import com.ac.kr.academy.domain.notice.Notice;
import com.ac.kr.academy.mapper.notice.NoticeMapper;
import com.ac.kr.academy.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService{
    private final NoticeMapper noticeMapper;
    private final NotificationService notificationService;

    //공지 생성 -> 알림 발송
    @Override
    public void createNotice(Notice notice) {
        //디버깅 - try
        try{

            noticeMapper.insertNotice(notice);  //DB에 저장

            //긴급(중요)일때 전체 사용자에게 알림 생성(발송) -> 알림서비스에서 구현
            if(notice.getIsUrgent()==1){
                notificationService.sendAllUser(notice.getId(), notice.getTitle());
            }
        }catch (Exception e){
            log.error("공지 생성/알림 바롱 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw  new RuntimeException("공지 생성 중 오류 발생", e);
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
    public Notice editNotice(Notice notice) {
        int updatedRows = noticeMapper.updateNotice(notice);
        if(updatedRows == 0){
            throw new RuntimeException("해당 공지가 존재하지 않습니다.");
        }

        //수정된 공지 재조회 - isUrgent 값 확인
        Notice updatedNotice = noticeMapper.findByNoticeId(notice.getId());

        //긴급 체크시 알림 발송 로직 추가
        if(updatedNotice.getIsUrgent() == 1){
            notificationService.sendAllUser(updatedNotice.getId(), updatedNotice.getTitle());
        }

        return noticeMapper.findByNoticeId(notice.getId());
    }

    //삭제
    @Override
    public boolean removeNotice(Long id) {
        int deletedRows = noticeMapper.deleteNotice(id);
        return deletedRows > 0;
    }
}