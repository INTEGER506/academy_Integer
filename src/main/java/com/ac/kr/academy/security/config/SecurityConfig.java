package com.ac.kr.academy.security.config;

import com.ac.kr.academy.security.CustomUserDetailsService;
import com.ac.kr.academy.security.jwt.JwtAuthenticationFilter;
import com.ac.kr.academy.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider tokenProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)    //폼 로그인,세션 비활성화 -> JWT 방식 활용
                .and()
                .cors(); //CORS 활성화 (AJAX 호출 시)

        //권한
        http.authorizeRequests()
                // 기본 접근 허용 (비로그인 사용자도 접근 가능)
                .antMatchers(
                        "/",              // 루트
                        "/index",         // index.jsp
                        "/login",         // 로그인 화면
                        "/auth/login",
                        "/api/auth/login",// 로그인 API
                        "/logout",        // 로그아웃 URL
                        "/auth/find-password",
                        "/api/auth/find-password", //비번 찾기
                        "/error", "/error/**", // 에러 페이지
                        "/.well-known/**"
                ).permitAll()

                // 정적 리소스 (CSS, JS, 이미지, 파비콘)
                .antMatchers(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/img/**",
                        "/favicon.ico",
                        "/favicon-*.png"
                ).permitAll()

                // 관리자 권한이 필요한 페이지
                .antMatchers(
                        "/admin/**",
                        "/api/admin/**",
                        "/auth/log-monitor",
                        "/auth/log-history",
                        "/auth/assign-advisor",
                        "/grade/admin/**"
                ).hasRole("ADMIN")

                // 학생 권한이 필요한 페이지
                .antMatchers(
                        "/enrollments/**",
                        "/student-timetable/**",
                        "/grade-view/**",
                        "/grade/student/**"
                ).hasRole("STUDENT")

                .antMatchers(
                        "/auth/**",
                        "/api/mypage/**",   //마이페이지
                        "/notificationList/**",
                        "/api/notifications/**"
                ).authenticated()

                // 교수 권한이 필요한 페이지
                 .antMatchers(
                         "/professor/**",
                         "/grade/professor/**"
                 ).hasAnyRole("PROFESSOR", "ADVISOR")

                // 그 외 요청은 모두 인증 필요
                .anyRequest().authenticated();


        //JWT 필터 (스프링 기본 필터 이전에 추가)
        http.addFilterBefore(
                new JwtAuthenticationFilter(tokenProvider, userDetailsService),
                UsernamePasswordAuthenticationFilter.class
        );


        //401, 403 에러 로깅 핸들러 추가
        http.exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    org.slf4j.LoggerFactory.getLogger(SecurityConfig.class)
                            .warn("Authentication Error: Status 401 Unauthorized - {}", request.getRequestURI());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    org.slf4j.LoggerFactory.getLogger(SecurityConfig.class)
                            .warn("Authentication Error: Status 403 Forbidden - {}", request.getRequestURI());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                });

        return http.build();

    }

    // CORS 정책 세부 설정 - 추가
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 모든 도메인 허용 (*) -> 필요할 수 있음.
        configuration.addAllowedOriginPattern("*");

        // 허용할 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 모든 헤더 허용
        configuration.addAllowedHeader("*");

        // 인증정보(JWT 쿠키, Authorization 헤더) 허용
        configuration.setAllowCredentials(true);

        // 프론트엔드에서 읽을 수 있도록 Authorization 헤더 노출
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
