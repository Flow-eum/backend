package com.flow.eum_backend.supervision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Table(name = "supervision_shares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupervisionShare {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "supervision_request_id", nullable = false, columnDefinition = "uuid")
    private UUID supervisionRequestId;

    /*
        공유 대상 리소스 타입
        - "session_record"
        - "document"
     */
    @Column(name = "record_type", nullable = false)
    private String recordType;

    @Column(name = "record_id", nullable = false, columnDefinition = "uuid")
    private UUID recordId;

    @Column(name = "wrapped_dek_for_supervisor", nullable = false, columnDefinition = "text")
    private String wrappedDekForSupervisor;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
