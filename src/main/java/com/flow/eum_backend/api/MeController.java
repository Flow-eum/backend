package com.flow.eum_backend.api;

import com.flow.eum_backend.auth.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// Jwt 인증이 제대로 동작하는지 테스트하기 위한 컨트롤러
@RestController
@Tag(name = "Auth Test", description = "Supabase JWT 인증 테스트용 api")
public class MeController {

    private final CurrentUser currentUser;

    public MeController(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @GetMapping("/api/me")
    @Operation(summary = "현재 로그인한 사용자 ID 조회", description = "Supabase JWT에서 추출한 userId(UUID)를 반환합니다.")
    public String me() {
        // CurrentUser 유틸을 통해 SecurityContext에서 userId 가져오기
        UUID userId = currentUser.getUserIdOrThrow();

        return "current user id = " + userId;
    }

}
