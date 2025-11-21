package com.flow.eum_backend.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

// SecurityContext에서 현재 로그인한 Supabase userId를 꺼내는 유틸리티
@Component
public class CurrentUser {

    public UUID getUserIdOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        if (auth instanceof SupabaseJwtFilter.SupabaseUserAuthentication supabaseAuth) {
            return (UUID) supabaseAuth.getPrincipal();
        }

        throw new IllegalStateException("Unexpected authentication type: " + auth);
    }

}
