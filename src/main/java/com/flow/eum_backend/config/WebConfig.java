package com.flow.eum_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins("http://localhost:3000") // <-- **프론트엔드 주소 지정**
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // <-- **허용할 메서드 지정**
                .allowedHeaders("Content-Type", "Authorization") // <-- **허용할 헤더 지정**
                .allowCredentials(true); // <-- 인증 정보 허용
    }
}
