package com.example.sns.config;

import com.example.sns.User.Details.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// 현재 로그인한 사용자의 정보를 가져오는 유틸리티 클래스
public class SecurityUtils {

    // 현재 로그인한 사용자의 ID를 반환하는 메서드
    public static Long getCurrentUserId() {
        // Spring Security의 SecurityContext에서 현재 인증(Authentication) 객체를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체가 없거나, 사용자가 인증되지 않았다면 null 반환
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // 인증된 사용자가 CustomUserDetails 타입이라면 해당 객체에서 userId를 가져옴
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }

        // 위의 조건을 만족하지 않으면 null 반환
        return null;
    }
}
