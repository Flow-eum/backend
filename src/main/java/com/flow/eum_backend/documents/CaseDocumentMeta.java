package com.flow.eum_backend.documents;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/*
    case_document_meta 테이블고 매핑
 */
@Entity
@Table(name = "case_document_meta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseDocumentMeta {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "case_id", nullable = false, columnDefinition = "uuid")
    private UUID caseId;

    /*
        문서 유형
     */
    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "s3_key",nullable = false)
    private String s3Key;

    @Column(name = "content_sha256", length = 64, nullable = false)
    private String contentSha256;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "created_by_user_id", nullable = false, columnDefinition = "uuid")
    private UUID createdByUserId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
