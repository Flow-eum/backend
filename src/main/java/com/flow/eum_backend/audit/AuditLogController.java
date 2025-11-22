package com.flow.eum_backend.audit;

import com.flow.eum_backend.auth.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "AuditLogs", description = "감사 로그 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUser currentUser;

    public AuditLogController(AuditLogRepository auditLogRepository, CurrentUser currentUser) {
        this.auditLogRepository = auditLogRepository;
        this.currentUser = currentUser;
    }

    @GetMapping("/me")
    @Operation(summary = "내가 수행한 로그 목록", description = "현재 로그인한 사용자가 수행한 행위 로그를 최신 순으로 반환합니다.")
    public ResponseEntity<List<AuditLog>> getMyAuditLogs() {
        UUID userId = currentUser.getUserIdOrThrow();
        List<AuditLog> logs = auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(logs);
    }
}
