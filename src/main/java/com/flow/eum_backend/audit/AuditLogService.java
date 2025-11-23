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

    public void logCurrentUserAction(
            String action,
            String resourceType,
            UUID resourceId,
            Map<String, Object> metadata
    ) {
        UUID userId = currentUser.getUserIdOrThrow();

        AuditLog log = AuditLog.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .createdAt(OffsetDateTime.now())
                .metadata(metadata)
                .build();

        auditLogRepository.save(log);
    }


}
