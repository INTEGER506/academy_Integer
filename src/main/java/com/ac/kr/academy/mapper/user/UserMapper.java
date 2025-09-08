package com.ac.kr.academy.mapper.user;

import com.ac.kr.academy.domain.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 긴급공지 전체 사용자 알림 때문에 임시 생성 / 성적
 * */
@Mapper
public interface UserMapper {

    //등록
    int insertUser(User user);

    //수정
    int updateUser(User user);

    //전체 사용자 조회
    List<Long> findAllUsersId();

    //사용자 조회
    User findById(Long id);

    //권한에 따라 사용자 조회
    List<Long> findAllUsersByRole(@Param("role") String role);

}
