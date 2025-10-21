package com.ac.kr.academy.mapper.notice;

import com.ac.kr.academy.domain.notice.Notice;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface NoticeMapper {
    //공지 작성
    void insertNotice(Notice notice);

    //공지 전체 조회
    List<Notice> getAllNotices();

    //공지 상세 조회
    Notice findByNoticeId(Long id);

    //공지 수정
    int updateNotice(Notice notice);

    //공지 삭제
    int deleteNotice(Long id);

    //조회수 증가
    void increaseViewCount(Long id);

    //미리알림 중복방지
    void updateDeadlineStatus(Long id);
}
