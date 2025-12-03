package com.flow.eum_backend.supervision.dto;

import com.flow.eum_backend.supervision.SupervisionRequest;
import com.flow.eum_backend.supervision.SupervisionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SupervisionRequestResponse(
        UUID id,
        UUID caseId,
        UUID requesterUserId,
        UUID supervisorUserId,
        SupervisionStatus status,
        String reason,
        OffsetDateTime allowedFrom,
        OffsetDateTime allowedUntil,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static SupervisionRequestResponse fromEntity(SupervisionRequest e) {
        return new SupervisionRequestResponse(
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
