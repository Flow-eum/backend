package com.flow.eum_backend.assessment.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record NeedAssessmentResponse(
        UUID id,
        UUID caseId,
        String assessmentType,
        LocalDate assessmentDate,
        UUID preparedByUserId,
        String note,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<NeedAssessmentItemResponse> items,
        List<NeedAssessmentScaleResponse> scales
) {}
