package com.flow.eum_backend.cases.dto;

import com.flow.eum_backend.cases.CaseEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CaseDetailDto(
        UUID id,
        String displayCode,
        String title,
        String status,
        UUID createdByUserId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<CaseMemberDto> members
) {
    public static CaseDetailDto fromEntity(
            CaseEntity c,
            List<CaseMemberDto> members
    ) {
        return new CaseDetailDto(
                c.getId(),
                c.getDisplayCode(),
                c.getTitle(),
                c.getStatus(),
                c.getCreatedByUserId(),
                c.getCreatedAt(),
                c.getUpdatedAt(),
                members
        );
    }
}
