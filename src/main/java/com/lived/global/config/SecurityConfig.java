package com.lived.global.config; // 본인 패키지 경로에 맞게 수정

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // POST 테스트를 위해 CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/temp/**").permitAll() // /temp로 시작하는 모든 주소는 로그인 없이 허용
                        .anyRequest().authenticated()            // 나머지는 여전히 로그인 필요
                );

        return http.build();
    }
}