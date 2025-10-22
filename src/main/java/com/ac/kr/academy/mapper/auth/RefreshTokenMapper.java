package com.ac.kr.academy.mapper.auth;

import com.ac.kr.academy.domain.auth.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {
    void save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Long userId);
}
