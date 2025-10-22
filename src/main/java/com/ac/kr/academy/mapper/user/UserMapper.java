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

    //전체 사용자 ID 조회 - 알림
    List<Long> findAllUserIds();

    //전체 사용자 조회
    List<User> findAllUsers();

    //권한별 사용자 조회
    List<User> findAllUsersByRole(@Param("role") String role);

    /**
     * 특정 사용자 ID의 역할(Role)을 조회합니다.
     * @param userId 사용자 ID
     * @return 사용자의 역할 문자열 (예: "ROLE_ADMIN")
     */
    String findRoleById(@Param("userId") Long userId);

    //ID로 사용자 조회
    User findById(Long id);

    //사용자 로그인 ID 조회 - 로그인
    User findByUsername(@Param("username") String username);

    //비밀번호 초기화, 변경
    void updateUserPassword(@Param("username") String username,
                            @Param("password") String password,
                            @Param("passwordTemp") boolean passwordTemp);

    //비밀번호 찾기
    User findUserForPasswordRecovery(@Param("username") String username,
                                     @Param("name") String name,
                                     @Param("email") String email);

    //로그인 전용
    User findByUsernameForLogin(String username);
}