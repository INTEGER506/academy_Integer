package com.ac.kr.academy.service.notice;

import com.ac.kr.academy.domain.notice.Notice;
import com.ac.kr.academy.mapper.notice.NoticeMapper;
import com.ac.kr.academy.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{
    private final NoticeMapper noticeMapper;
    private final NotificationService notificationService;

    /**
     * 성적 관련 학생에게만 보내야할 경우 메서드 수정 필요
     *  ㄴ 모든 사용자에게 다 보낼경우 간단히 수정 필요
     * */
    //공지 생성 -> 알림 발송
    @Override
    public void createNotice(Notice notice) {
        noticeMapper.insertNotice(notice);  //DB에 저장

        //긴급(중요)일때 전체 사용자에게 알림 생성(발송) -> 알림서비스에서 구현
        if(notice.getIsUrgent()==1){
            notificationService.sendAllUser(notice.getId(), notice.getTitle());
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
    public void editNotice(Notice notice) {
        noticeMapper.updateNotice(notice);
    }

    //삭제
    @Override
    public void removeNotice(Long id) {
        noticeMapper.deleteNotice(id);
    }
}
