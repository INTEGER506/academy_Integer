package com.ac.kr.academy.mapper.dept;


import com.ac.kr.academy.domain.dept.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeptMapper {

    void insert(Dept dept);

    Dept findById(Long id);

    List<Dept> findAll();

    void update(Dept dept);

    void deleteById(Long id);

    //학과 ID로 학과 이름 조회
    String findDeptNameById(@Param("id") Long id);
}
