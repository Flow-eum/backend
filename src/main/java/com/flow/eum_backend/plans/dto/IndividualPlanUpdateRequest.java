package com.flow.eum_backend.plans.dto;

public record IndividualPlanUpdateRequest(
        String title,
        String planStatus,
        String s3Key,
        String contentSha256
) {
}
