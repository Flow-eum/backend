package com.flow.eum_backend.sessions.dto;

import com.flow.eum_backend.sessions.SessionRecordMeta;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionRecordDetailDto(
        UUID id,
        UUID caseId,
        Integer seq,
        String title,
        LocalDate sessionDate,
        String s3Key,
        String contentSha256,
        Long sizeBytes,
        Integer version,
        UUID createdByUserId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static SessionRecordDetailDto fromEntity(SessionRecordMeta e) {
        return new SessionRecordDetailDto(
                e.getId(),
                e.getCaseId(),
                e.getSeq(),
                e.getTitle(),
                e.getSessionDate(),
                e.getS3Key(),
                e.getContentSha256(),
                e.getSizeBytes(),
                e.getVersion(),
                e.getCreatedByUserId(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
