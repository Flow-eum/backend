package com.flow.eum_backend.cases.dto;

import com.flow.eum_backend.cases.CaseEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CaseSummaryDto(
        UUID id,
        String displayCode,
        String title,
        String status,
        OffsetDateTime updatedAt
) {
    public static CaseSummaryDto fromEntity(CaseEntity c) {
        return new CaseSummaryDto(
                c.getId(),
                c.getDisplayCode(),
                c.getTitle(),
                c.getStatus(),
                c.getUpdatedAt()
        );
    }
}
