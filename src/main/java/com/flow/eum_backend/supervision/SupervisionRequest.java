package com.flow.eum_backend.supervision;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "supervision_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupervisionRequest {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "case_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID caseId;

    /**
     * 요청을 보낸 사람 (A, 열람하고 싶은 사람)
     */
    @Column(name = "requester_user_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID requesterUserId;

    /**
     * 요청을 받은 사람 (B, case 담당자/슈퍼바이저)
     */
    @Column(name = "supervisor_user_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID supervisorUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SupervisionStatus status;

    @Column(name = "reason")
    private String reason;

    @Column(name = "allowed_from")
    private OffsetDateTime allowedFrom;

    @Column(name = "allowed_until")
    private OffsetDateTime allowedUntil;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (status == null) status = SupervisionStatus.PENDING;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

}
