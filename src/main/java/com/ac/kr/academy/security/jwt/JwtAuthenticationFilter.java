package com.ac.kr.academy.security.jwt;

import com.ac.kr.academy.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
//모든 요청이 서버에 도달하기 전에 가장 먼저 JWT 토큰을 검증하는 역할
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = null;

        //Authorization 헤더가 비어있지 않고 "Bearer "로 시작할 경우
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")){
            token = bearer.substring(7);
        }

        //헤더에 없을 경우 쿠키에서 토큰 찾기
        if(!StringUtils.hasText(token) && request.getCookies() != null){
            for(Cookie c : request.getCookies()){
                if("ACCESS_TOKEN".equals(c.getName())){ //이름이 ACCESS_TOKEN인 쿠키를 token에 저장
                    token = c.getValue();
                    break;
                }
            }
        }

        //유효한 JWT 인지 검증
        if(StringUtils.hasText(token) && tokenProvider.validateToken(token)){
            String username = tokenProvider.getUsername(token);
            UserDetails ud = userDetailsService.loadUserByUsername(username);

            //스프링 시큐리티 표준 Authentication 객체 생성
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.debug("JWT 인증 성공 - 사용자: {}", username);
        }
        filterChain.doFilter(request, response);
    }
}
