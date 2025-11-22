package com.flow.eum_backend.sessions.dto;

import com.flow.eum_backend.sessions.SessionRecordMeta;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionRecordSummaryDto(
        UUID id,
        Integer seq,
        String title,
        LocalDate sessionDate,
        Integer version,
        OffsetDateTime updatedAt
) {
    public static SessionRecordSummaryDto fromEntity(SessionRecordMeta e) {
        return new SessionRecordSummaryDto(
                e.getId(),
                e.getSeq(),
                e.getTitle(),
                e.getSessionDate(),
                e.getVersion(),
                e.getUpdatedAt()
        );
    }
}
