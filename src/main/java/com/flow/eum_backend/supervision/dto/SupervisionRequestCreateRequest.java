package com.flow.eum_backend.supervision.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SupervisionRequestCreateRequest(
        UUID caseId,
        UUID supervisorUserId,
        String reason
) {
}
