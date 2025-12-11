package com.flow.eum_backend.assessment;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "need_assessment_scales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NeedAssessmentScale {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private NeedAssessment assessment;

    @Column(name = "scale_name", length = 200, nullable = false)
    private String scaleName;

    @Column(columnDefinition = "text")
    private String result;

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
