package com.flow.eum_backend.supervision.dto;

import com.flow.eum_backend.supervision.SupervisionShare;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SupervisionShareDto(
        UUID id,
        UUID supervisionRequestId,
        String recordType,
        UUID recordId,
        String wrappedDekForSupervisor,
        OffsetDateTime expiresAt,
        OffsetDateTime revokedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static SupervisionShareDto fromEntity(SupervisionShare e) {
        return new SupervisionShareDto(
                e.getId(),
                e.getSupervisionRequestId(),
                e.getRecordType(),
                e.getRecordId(),
                e.getWrappedDekForSupervisor(),
                e.getExpiresAt(),
                e.getRevokedAt(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
