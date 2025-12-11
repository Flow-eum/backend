package com.flow.eum_backend.assessment.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record NeedAssessmentCreateRequest(
        String assessmentType,                     // "INITIAL" 등
        LocalDate assessmentDate,
        UUID preparedByUserId,
        String note,
        List<NeedAssessmentItemRequest> items,
        List<NeedAssessmentScaleRequest> scales
) {}
