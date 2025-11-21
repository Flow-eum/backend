package com.flow.eum_backend.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component // JWT 설정값을 보관하는 클래스
public class SupabaseJwtProperties {

    private final String secret;

    private final String issuer;

    public SupabaseJwtProperties(
            @Value("${supabase.jwt.secret}") String secret,
            @Value("${supabase.jwt.issuer:}") String issuer
    ) {
        this.secret = secret;
        this.issuer = issuer;
    }

    public String getSecret() {
        return secret;
    }

    public String getIssuer() {
        return issuer;
    }

}
