package com.ac.kr.academy.mapper.dept;


import com.ac.kr.academy.domain.dept.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeptMapper {

    void insert(Dept dept);

    Dept findById(Long id);

    List<Dept> findAll();

    void update(Dept dept);

    void deleteById(Long id);
}
