package com.flow.eum_backend.supervision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

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
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "case_id", nullable = false, columnDefinition = "uuid")
    private UUID caseId;

    @Column(name = "requester_user_id", nullable = false, columnDefinition = "uuid")
    private UUID requesterUserId;

    @Column(name = "supervisor_user_id", nullable = false, columnDefinition = "uuid")
    private UUID supervisorUserId;

    /*
        요청 상태
        - pending: 대기중
        - approved: 승인됨
        - rejected: 거절됨
        - revoked: 요청자가 중간에 취소
     */
    @Column(name = "status", nullable = false)
    private String status;

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

}
