package com.lived.global.config;

import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.jwt.JwtAuthenticationFilter;
import com.lived.global.oauth2.handler.OAuth2SuccessHandler;
import com.lived.global.oauth2.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .formLogin(form -> form.disable()) // 기본 로그인 페이지 비활성화
                .httpBasic(basic -> basic.disable()) // HTTP Basic 인증 비활성화

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/login/**",
                                "/oauth2/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-resources/**"
                        ).permitAll() // 로그인 관련, 스웨거 허용

                        .anyRequest().authenticated() // 그 외는 인증 필요
                )

                // 인증 실패 시 응답
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {

                            com.lived.global.apiPayload.code.BaseErrorCode errorCode = com.lived.global.apiPayload.code.GeneralErrorCode.UNAUTHORIZED;
                            response.setStatus(errorCode.getStatus().value());
                            response.setContentType("application/json;charset=UTF-8");

                            String jsonResponse = String.format(
                                    "{\"isSuccess\":false,\"code\":\"%s\",\"message\":\"%s\",\"result\":null}",
                                    errorCode.getCode(),
                                    errorCode.getMessage()
                            );

                            response.getWriter().write(jsonResponse);
                        })
                )

                // OAuth2 로그인 설정 추가
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}