package com.flow.eum_backend.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class SupabaseJwtFilter extends OncePerRequestFilter {

    private final SupabaseJwtService jwtService;

    @Override
    protected void doFilterInternal (
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // JWT 검증을 아에 하지 않을 공개 엔드포인트는 여기서 패스
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에서 토큰 꺼내기
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 토큰이 없으면 익명 사용자로 진행
            // 나중에 SecurityConfig에서 이 경로를 authenticated()로 걸어두면 401이 떨어짐.
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();

        try {
            // 토큰 검증 + userId 추출
            UUID userId = jwtService.extractUserId(token);

            AbstractAuthenticationToken authentication =
                    new SupabaseUserAuthentication(userId);

            // SecurityContext에 Authentication 세팅
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 다음 필터/컨트롤러로 진행
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // 토큰 검증에 실패하면 401 Unauthorized 반환
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                    {"error":"invalid_token","message": "%s"}
                    """.formatted(e.getMessage()));
        }

    }

    // 인증 없이 접근 허용 경로 정의
    private boolean isPublicPath(String path) {
        return path.startsWith("/health")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator");
    }

    public static class SupabaseUserAuthentication extends AbstractAuthenticationToken {

        private final UUID userId;

        public SupabaseUserAuthentication(UUID userId) {
            super(List.of(new SimpleGrantedAuthority("ROLE_USER")));
            this.userId = userId;
            // 토큰 검증을 마쳤기 때문에 인증 완료 상태로 표시
            setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return userId;
        }
    }

}
