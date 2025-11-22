package com.flow.eum_backend.plans.dto;

import com.flow.eum_backend.plans.IndividualPlan;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IndividualPlanDetailDto(
        UUID id,
        UUID caseId,
        Integer versionNo,
        String title,
        String planStatus,
        String s3Key,
        String contentSha256,
        UUID preparedByUserId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static IndividualPlanDetailDto fromEntity(IndividualPlan e) {
        return new IndividualPlanDetailDto(
                e.getId(),
                e.getCaseId(),
                e.getVersionNo(),
                e.getTitle(),
                e.getPlanStatus(),
                e.getS3Key(),
                e.getContentSha256(),
                e.getPreparedByUserId(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
