package com.flow.eum_backend.supervision.dto;

import com.flow.eum_backend.supervision.SupervisionRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SupervisionRequestDto(
        UUID id,
        UUID caseId,
        UUID requesterUserId,
        UUID supervisorUserId,
        String status,
        String reason,
        OffsetDateTime allowedFrom,
        OffsetDateTime allowedUntil,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static SupervisionRequestDto fromEntity(SupervisionRequest e) {
        return new SupervisionRequestDto(
                e.getId(),
                e.getCaseId(),
                e.getRequesterUserId(),
                e.getSupervisorUserId(),
                e.getStatus(),
                e.getReason(),
                e.getAllowedFrom(),
                e.getAllowedUntil(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
