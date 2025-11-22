package com.flow.eum_backend.documents.dto;

/*
    사례 문서 메타 수정 요청 DTO
 */
public record CaseDocumentUpdateRequest(
        String title,
        String s3Key,
        String contentSha256,
        Long sizeBytes
) {
}
