package com.flow.eum_backend.documents.dto;

import com.flow.eum_backend.documents.CaseDocumentMeta;

import java.time.OffsetDateTime;
import java.util.UUID;

/*
    문서 목록 조회용 요약 DTO
 */
public record CaseDocumentSummaryDto(
        UUID id,
        String documentType,
        String title,
        Long sizeBytes,
        Integer version,
        OffsetDateTime updatedAt
) {
    public static CaseDocumentSummaryDto fromEntity(CaseDocumentMeta e) {
        return new CaseDocumentSummaryDto(
                e.getId(),
                e.getDocumentType(),
                e.getTitle(),
                e.getSizeBytes(),
                e.getVersion(),
                e.getUpdatedAt()
        );
    }
}
