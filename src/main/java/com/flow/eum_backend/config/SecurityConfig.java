package com.flow.eum_backend.config;

import com.flow.eum_backend.auth.SupabaseJwtFilter;
import com.flow.eum_backend.auth.SupabaseJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


// Spring Security 전역 설정
//  - Jwt stateless 인증 사용
//  - /health, /swagger-ui, /v3/api-docs 등은 인증 없이 허용
//  - 나머지 /api/**는 기본적으로 인증 필요
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final SupabaseJwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 우리 커스텀 JWT 필터 생성
        SupabaseJwtFilter jwtFilter = new SupabaseJwtFilter(jwtService);

        http
                // REST api라 csrf 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 세션을 서버에서 관리 X, 매 요청마다 Jwt로 인증하는 형태
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))

                // 엔드포인트별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 공개 경로
                        .requestMatchers("/health/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 나머지 api는 인증 필요
                        .requestMatchers("/api/**").authenticated()
                        // 그 외는 일단 다 허용
                        .anyRequest().permitAll()
                )

                // 폼 로그인 / HTTP Basic 같은 것은 사용 안함
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // UsernamePasswordAuthenticationFilter 전에 우리 Jwt 필터 넣기
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // 기타 기본 설정
                .cors(Customizer.withDefaults());

        return http.build();
    }
}
