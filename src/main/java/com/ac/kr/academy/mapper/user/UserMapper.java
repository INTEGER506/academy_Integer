package com.ac.kr.academy.mapper.user;

import com.ac.kr.academy.domain.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    void insertUser(User user);

    void updateUser(User user);

    void deleteUser(Long id);

    //전체 사용자 조회
    List<Long> findAllUserIds();

    //ID로 사용자 조회
    User findById(Long id);

    //권한에 따라 사용자 조회 (알림용)
    List<Long> findAllUsersByRole(@Param("role") String role);


    //사용자 정보와 권한 조회 (로그인용)
    User findByUsername(@Param("username") String username);

    //비밀번호 초기화, 변경
    void updateUserPassword(@Param("username") String username,
                            @Param("password") String password,
                            @Param("passwordTemp") boolean passwordTemp);

    //AdminRestController 로그인 ID 자동생성에서 사용
    int updateUsername(@Param("id") Long id, @Param("username") String username);
}
