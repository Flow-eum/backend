package com.flow.eum_backend.documents.dto;

/*
    사례 문서 메타 생성 요청 DTO
 */
public record CaseDocumentCreateRequest(
        String documentType,
        String title,
        String s3Key,
        String contentSha256,
        Long sizeBytes
) {
}
