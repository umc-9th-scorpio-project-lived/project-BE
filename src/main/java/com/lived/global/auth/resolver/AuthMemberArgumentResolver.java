package com.lived.global.auth.resolver;

import com.lived.global.auth.annotation.AuthMember;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @AuthMember가 붙어 있고 타입이 Long이면 동작
        return parameter.hasParameterAnnotation(AuthMember.class) &&
                (parameter.getParameterType().equals(Long.class) || parameter.getParameterType().equals(long.class));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            return null; // 인증 정보가 없으면 null 반환
        }

        // member_id 반환
        return (Long) authentication.getPrincipal();
    }
}
