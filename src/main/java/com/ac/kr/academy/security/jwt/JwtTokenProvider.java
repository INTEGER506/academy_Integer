package com.ac.kr.academy.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenProvider { //토큰 생성, 유효성 검증, 토큰에서 정보 추출하는 역할
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-token-validity-seconds}")
    private long accessValiditySeconds;

    //추가
    @Value("${app.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    private Key secretKey;

    @PostConstruct
    public void init(){ // Base64 문자열을 디코딩해서 HS512 키 생성
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    //Access_Token 발급
    public String generateAccessToken(Authentication authentication){
        return generateToken(authentication, false);
    }

    //Refresh Token 발급
    public String generateRefreshToken(Authentication authentication){
        return generateToken(authentication, true);
    }

    //Access/Refresh Token 동시 발급
    public Map<String, String> generateTokenPair(Authentication authentication){
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", generateAccessToken(authentication));
        tokens.put("refreshToken", generateRefreshToken(authentication));
        return tokens;
    }

    // 토큰 생성 공통 로직
    public String generateToken(Authentication authentication, boolean isRefresh){
        Date now = new Date();

        //Refresh 토큰인지, Access 토큰인지에 따라 유효시간 다르게 계산
        long validitySeconds = isRefresh ? refreshTokenValiditySeconds : accessValiditySeconds;

        //설정에 넣은 값을 토큰 생성 시 만료일(expiry) 계산에 사용
        Date expiry = new Date(now.getTime() + validitySeconds * 1000);

        //토큰 빌더
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("authorities", authentication.getAuthorities())  //권한을 커스텀 클레임으로 저장
                .setIssuedAt(now)       //발급 시각
                .setExpiration(expiry)  //만료 시각
                .signWith(secretKey, SignatureAlgorithm.HS512)  //HS512 알고리즘으로 서명
                .compact(); //최종 JWT 문자열 생성
    }

    //JWT 토큰에서 사용자 아이디(Subject(username)) 추출
    public String getUsername(String token){
        return parseClaims(token).getBody().getSubject();
    }

    //JWT 토큰 유효성 검증
    public boolean validateToken(String token){
        try{
            parseClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    //JWT 토큰 파싱(객체반환)
    private Jws<Claims> parseClaims(String token){
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
    }

}
