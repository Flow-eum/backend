package com.flow.eum_backend.supervision.dto;

import java.time.OffsetDateTime;

public record SupervisionRequestUpdateStatusRequest(
        String status,
        OffsetDateTime allowedFrom,
        OffsetDateTime allowedUntil
) {
}
