package com.flow.eum_backend.assessment;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "need_assessment_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NeedAssessmentItem {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private NeedAssessment assessment;

    @Column(length = 50, nullable = false)
    private String category;

    @Column(name = "sub_category", length = 100, nullable = false)
    private String subCategory;

    @Column(name = "detail", columnDefinition = "jsonb")
    private String detailJson; // Map<String,Object> 를 JSON 문자열로 직렬화해서 저장

    @Column(name = "need_level")
    private Integer needLevel;

    @Column(name = "priority")
    private Integer priority;

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
