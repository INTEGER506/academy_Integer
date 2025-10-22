package com.ac.kr.academy.service.calendar;

import com.ac.kr.academy.domain.calendar.Calendar;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CalendarService {

    /**
     * 일정 생성 또는 수정. ID가 있으면 수정, 없으면 생성합니다.
     */
    void saveCalendar(Calendar calendar);

    /**
     * ID로 단일 일정을 조회합니다.
     */
    Calendar findById(Long id);

    /**
     * 모든 학사 일정을 조회합니다.
     */
    List<Calendar> findAll();

    /**
     * 특정 날짜 범위의 일정을 조회합니다.
     */
    List<Calendar> getCalendarsByDate(String date);

    /**
     * 일정을 삭제합니다.
     */
    void delete(Long id);
}
