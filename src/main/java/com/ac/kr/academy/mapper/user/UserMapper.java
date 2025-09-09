package com.ac.kr.academy.mapper.user;

import com.ac.kr.academy.domain.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    //등록
    int insertUser(User user);

    //수정
    int updateUser(User user);

    //전체 사용자 조회
    List<Long> findAllUserIds();

    //ID로 사용자 조회
    User findById(Long id);

    //권한에 따라 사용자 조회 (알림용)
    List<Long> findAllUsersByRole(@Param("role") String role);


    //사용자 정보와 권한 조회 (로그인용) -> email 컬럼 사용
    User findByUsername(String username);

}
