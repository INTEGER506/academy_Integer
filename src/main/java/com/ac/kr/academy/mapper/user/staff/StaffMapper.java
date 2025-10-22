package com.ac.kr.academy.mapper.user.staff;

import com.ac.kr.academy.domain.user.Staff;
import com.ac.kr.academy.dto.user.MyPageInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StaffMapper {

    void insertStaff(Staff staff);
    int updateStaff(Staff staff);
    int deleteStaff(@Param("id") Long userId);

    Staff findByUserId(@Param("userId") Long userId);

    //마이페이지용
    MyPageInfoDTO findMyPageInfoByUserId(@Param("userId") Long userId);
}