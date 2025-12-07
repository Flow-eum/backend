package com.flow.eum_backend.assessment.dto;

import java.util.UUID;

public record NeedAssessmentScaleResponse(
        UUID id,
        String scaleName,
        String result,
        String note,
        Integer sortOrder
) {}
