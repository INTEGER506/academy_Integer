package com.ac.kr.academy.mapper.calendar;


import com.ac.kr.academy.domain.calendar.Calendar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CalendarMapper {

    void insert(Calendar calendar);

    Calendar findById(Long id);

    List<Calendar> findAll();

    List<Calendar> findByDate(@Param("date") String date);

    void update(Calendar  calendar);

    void delete(Long id);
}
