package com.flow.eum_backend.profile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.UUID;

/*
Supabase Postgres의 profiles와 매핑되는 entity
 */
@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "can_supervise", nullable = false)
    @ColumnDefault("false")
    private boolean canSupervise; // 해당 사용자가 슈퍼비전 역할을 수행할 수 있을지 여부(기본값은 false)

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
