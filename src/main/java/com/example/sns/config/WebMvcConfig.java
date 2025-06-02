package com.example.sns.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // ✅ 캐시 방지 인터셉터 추가
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                return true;
            }
        });
    }

    // 강의실
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile-images/**").addResourceLocations("file:///C:/Users/User/Desktop/sns_2/profileImage/");

        registry.addResourceHandler("/post-images/**").addResourceLocations("file:///C:/Users/User/Desktop/sns_2/postImages/");
    }

// // 노트북
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/profile-images/**")
//                .addResourceLocations("file:///C:/Users/하태민/OneDrive/바탕 화면/sns_2/profileImage");
//
//        registry.addResourceHandler("/post-images/**")
//                .addResourceLocations("file:///C:/Users/하태민/OneDrive/바탕 화면/sns_2/postImages");
//    }


}

