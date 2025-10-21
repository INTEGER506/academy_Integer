package com.ac.kr.academy.service.auth;

import com.ac.kr.academy.domain.auth.RefreshToken;
import com.ac.kr.academy.mapper.auth.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenMapper refreshTokenMapper;

    public void saveRefreshToken(Long userId, String token, LocalDateTime expiryDate) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(expiryDate);

        //사용자별 1개만 유지
        refreshTokenMapper.deleteByUserId(userId);
        refreshTokenMapper.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenMapper.findByToken(token);
    }

    public void deleteByUserId(Long userId) {
        refreshTokenMapper.deleteByUserId(userId);
    }
}
