package com.ac.kr.academy.mapper.user.staff;

import com.ac.kr.academy.domain.user.Staff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StaffMapper {

    void insertStaff(Staff staff);
    int updateStaff(Staff staff);
    int deleteStaff(@Param("id") Long id);

    List<Staff> findAllStaff();
    Staff findById(@Param("id") Long id);
    Staff findByStaffNum(@Param("staffNum") String staffNum);

}
