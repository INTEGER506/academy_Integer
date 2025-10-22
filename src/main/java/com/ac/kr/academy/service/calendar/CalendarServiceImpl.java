package com.ac.kr.academy.service.calendar;


import com.ac.kr.academy.domain.calendar.Calendar;
import com.ac.kr.academy.mapper.calendar.CalendarMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarMapper calendarMapper;

    /**
     * 일정 생성 또는 수정.
     */
    @Override
    @Transactional
    public void saveCalendar(Calendar calendar) {
        if (calendar.getId() == null) {
            calendarMapper.insert(calendar);
        } else {
            calendarMapper.update(calendar);
        }
    }

    /**
     * ID로 단일 일정을 조회합니다.
     */
    @Override
    public Calendar findById(Long id) {
        return calendarMapper.findById(id);
    }

    /**
     * 모든 학사 일정을 조회합니다.
     */
    @Override
    public List<Calendar> findAll() {
        return calendarMapper.findAll();
    }

    /**
     * 특정 날짜 범위의 일정을 조회합니다.
     */
    @Override
    public List<Calendar> getCalendarsByDate(String date) {
        return calendarMapper.findByDate(date);
    }

    /**
     * 일정을 삭제합니다.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        calendarMapper.delete(id);
    }
}

