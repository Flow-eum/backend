package com.flow.eum_backend.plans.dto;

public record IndividualPlanCreateRequest(
        String title,
        String planStatus,
        String s3Key,
        String contentSha256
) {
}
