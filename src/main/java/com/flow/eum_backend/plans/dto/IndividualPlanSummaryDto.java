package com.flow.eum_backend.plans.dto;

import com.flow.eum_backend.plans.IndividualPlan;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IndividualPlanSummaryDto(
        UUID id,
        Integer versionNo,
        String title,
        String planStatus,
        OffsetDateTime updatedAt
) {
    public static IndividualPlanSummaryDto fromEntity(IndividualPlan e) {
        return new IndividualPlanSummaryDto(
                e.getId(),
                e.getVersionNo(),
                e.getTitle(),
                e.getPlanStatus(),
                e.getUpdatedAt()
        );
    }
}
