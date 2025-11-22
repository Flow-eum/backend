package com.flow.eum_backend.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    // 특정 사용자의 로그를 최근 순으로 조회 (디버깅/마이페이지 용도로 쓸 수 있음)
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // 특정 리소스에 대한 로그 조회 (예: 한 사례에 대한 접근 히스토리)
    List<AuditLog> findByResourceTypeAndResourceIdOrderByCreatedAtDesc(
            String resourceType,
            UUID resourceId
    );
}
