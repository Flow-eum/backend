package com.flow.eum_backend.sessions;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/*
    session_record_meta 테이블과 매핑되는 entity
 */
@Entity
@Table(
        name = "session_record_meta",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_session_case_seq",
                        columnNames = {"case_id", "seq"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionRecordMeta {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    // 이 회차가 속한 사례 ID
    @Column(name = "case_id", nullable = false, columnDefinition = "uuid")
    private UUID caseId;

    // 사례별 회차 번호
    @Column(name = "seq", nullable = false)
    private Integer seq;

    // 회차 제목
    @Column(name = "title", nullable = false)
    private String title;

    // 상담 진행 일자
    @Column(name = "session_date")
    private LocalDate sessionDate;

    /*
        암호화된 본문 파일 경로
        - Supabase Storage OR S3 버킷 내의 경로 문자열
     */
    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    // 암호문(파일)의 SHA-256 해시
    @Column(name = "content_sha256", length = 64, nullable = false)
    private String contentSha256;

    // 파일 크기
    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    /*
        회차 기록 버전
     */
    @Column(name = "version", nullable = false)
    private Integer version;

    // 이 회차 메타를 작성한 사용자
    @Column(name = "created_by_user_id", nullable = false, columnDefinition = "uuid")
    private UUID createdByUserId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "ai_outputs", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String aiOutputs;
}
