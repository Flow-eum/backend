package com.flow.eum_backend.assessment;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "assessment_structured")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentStructured {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "case_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID caseId;

    @Column(name = "assessment_type")
    private String assessmentType; // "initial", "reassessment" 등

    @Column(name = "assessment_date")
    private LocalDate assessmentDate;

    @Column(name = "prepared_by_user_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID preparedByUserId;

    /**
     * 사정 기록지 전체 내용을 jsonb로 저장하는 필드.
     * (client_basic, meeting, family_members 등 전부 포함)
     */
    @Column(name = "assessment_json", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String assessmentJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
