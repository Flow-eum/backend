package com.flow.eum_backend.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service // Jwt 검증하고, 토근 안에서 userId를 꺼내오는 역할 담당
public class SupabaseJwtService {

    private final SupabaseJwtProperties properties;
    private final SecretKey secretKey;

    public SupabaseJwtService(SupabaseJwtProperties properties) {
        this.properties = properties;

        // Supabase Jwt secret 문자열로부터 HMAC-SHA 키 생성
        this.secretKey = Keys.hmacShaKeyFor(
                properties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    // 토큰이 유효하면 Claims 반환, 유효하지 않으면 예외 던짐
    public Claims parseAndValidate(String token) {
        var jwtParser = Jwts.parser()
                .verifyWith(secretKey) // HMAC-SHA 서명 검증
                .build();

        var jws = jwtParser.parseSignedClaims(token);
        Claims claims = jws.getPayload();

        String expectedIssuer = properties.getIssuer();
        if (expectedIssuer != null && !expectedIssuer.isBlank()) {
            String tokenIssuer = claims.getIssuer();
            if (!expectedIssuer.equals(tokenIssuer)) {
                throw new IllegalArgumentException("Invalid JWT issuer");
            }
        }

        return claims;
    }

    public UUID extractUserId(String token) {
        Claims claims = parseAndValidate(token);

        // Supabase의 기본 Jwt는 sub에 auth.users.id(UUID)가 들어있음
        String sub = claims.getSubject();
        if (sub == null || sub.isBlank()) {
            throw new IllegalArgumentException("JWT subject (sub) is empty");
        }

        return UUID.fromString(sub);
    }


}
