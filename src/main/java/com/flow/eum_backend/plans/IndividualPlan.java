package com.flow.eum_backend.plans;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/*
    individual_plans 테이블과 매핑
 */
@Entity
@Table(name = "individual_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndividualPlan {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "case_id", nullable = false, columnDefinition = "uuid")
    private UUID caseId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "plan_status", nullable = false)
    private String planStatus;

    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    @Column(name = "content_sha256", length = 64, nullable = false)
    private String contentSha256;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @Column(name = "prepared_by_user_id", nullable = false, columnDefinition = "uuid")
    private UUID preparedByUserId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
