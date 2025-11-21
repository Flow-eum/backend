package com.flow.eum_backend.cases;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/*
    cases 테이블과 매핑되는 엔티티
 */
@Entity
@Table(name = "cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    /*
        - 사례 코드
        - 실제 이름 대신 식별용 코드로 사용
     */
    @Column(name = "display_code", nullable = false, unique = true)
    private String displayCode;

    /*
        - 상담자가 알아보기 위한 제목
     */
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "status", nullable = false)
    private String status; // "open", "closed", "suspended" 등

    /*
        - 이 케이스를 최초 생성한 사용자
     */
    @Column(name = "created_by_user_id", nullable = false, columnDefinition = "uuid")
    private UUID createdByUserId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
