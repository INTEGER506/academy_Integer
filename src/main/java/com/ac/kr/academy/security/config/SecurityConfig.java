package com.ac.kr.academy.security.config;

import com.ac.kr.academy.security.CustomUserDetailsService;
import com.ac.kr.academy.security.jwt.JwtAuthenticationFilter;
import com.ac.kr.academy.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider tokenProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
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
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);    //폼 로그인,세션 비활성화 -> JWT 방식 활용

        //권한
        http.authorizeRequests()
                .antMatchers("/login", "/api/auth/login", "/auth/**", "/css/**", "/error/**").permitAll()  //인증없이 접근가능
                .antMatchers("/admin/**").hasRole("ADMIN")  //ADMIN 권한만 가능
                .antMatchers("/").hasRole("USER")
                .anyRequest().authenticated();  //<-반드시 작성

        //JWT 필터 (스프링 기본 필터 이전에 추가)
        http.addFilterBefore(
                new JwtAuthenticationFilter(tokenProvider, userDetailsService),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
