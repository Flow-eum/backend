package com.flow.eum_backend.documents.dto;


import com.flow.eum_backend.documents.CaseDocumentMeta;

import java.time.OffsetDateTime;
import java.util.UUID;

/*
    단일 문서 메타 상세 조회 DTO
 */
public record CaseDocumentDetailDto(
        UUID id,
        UUID caseId,
        String documentType,
        String title,
        String s3Key,
        String contentSha256,
        Long sizeBytes,
        Integer version,
        UUID createdByUserId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static CaseDocumentDetailDto fromEntity(CaseDocumentMeta e) {
        return new CaseDocumentDetailDto(
                e.getId(),
                e.getCaseId(),
                e.getDocumentType(),
                e.getTitle(),
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
