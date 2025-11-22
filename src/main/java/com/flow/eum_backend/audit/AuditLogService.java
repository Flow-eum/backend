package com.flow.eum_backend.audit;

import com.flow.eum_backend.auth.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUser currentUser;

    /*
        현재 로그인한 사용자 기준으로 감사 로그를 남기는 편의 메서드
     */
    @Transactional
    public void logCurrentUserAction(
            String action,
            String resourceType,
            UUID resourceId,
            Map<String, Object> metadata
    ) {
        UUID userId = currentUser.getUserIdOrThrow();
        logAction(userId, action, resourceType, resourceId, metadata);
    }

    /*
        명시적으로 userId를 지정해서 감사 로그를 남기는 메서드
     */
    @Transactional
    public void logAction(
            UUID userId,
            String action,
            String resourceType,
            UUID resourceId,
            Map<String, Object> metadata
    ) {
        String metadataJson = null;
        if (metadata != null && !metadata.isEmpty()) {
            // 아주 간단한 직렬화: key=value;key2=value2... 이런 식으로만 넣어도 되고,
            // 필요하다면 Jackson ObjectMapper 를 주입해서 실제 JSON 으로 serialize 해도 된다.
            metadataJson = simpleSerialize(metadata);
        }

        AuditLog log = AuditLog.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .createdAt(OffsetDateTime.now())
                .metadata(metadataJson)
                .build();

        auditLogRepository.save(log);
    }

    private String simpleSerialize(Map<String, Object> metadata) {
        StringBuilder sb = new StringBuilder();
        metadata.forEach((k, v) -> {
            if (!sb.isEmpty()) sb.append("; ");
            sb.append(k).append("=").append(String.valueOf(v));
        });
        return sb.toString();
    }
}
