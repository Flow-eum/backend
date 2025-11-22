package com.flow.eum_backend.supervision.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SupervisionShareCreateRequest(
        String recordType,
        UUID recordId,
        String wrappedDekForSupervisor,
        OffsetDateTime expiresAt
) {
}
