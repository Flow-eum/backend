package com.flow.eum_backend.cases;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/*
    - case_members 테이블과 매핑되는 엔티티
 */
@Entity
@Table(
        name = "case_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_case_members_case_user",
                        columnNames = {"case_id", "user_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseMember {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "case_id", nullable = false, columnDefinition = "uuid")
    private UUID caseId;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    /*
        이 사례에서의 역할
     */
    @Column(name = "role", nullable = false)
    private String role;

    // 현재 활성 상태인저 여부
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
