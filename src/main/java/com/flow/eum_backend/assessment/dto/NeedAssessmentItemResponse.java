package com.flow.eum_backend.assessment.dto;

import java.util.Map;
import java.util.UUID;

public record NeedAssessmentItemResponse(
        UUID id,
        String category,
        String subCategory,
        Map<String, Object> detail,
        Integer needLevel,
        Integer priority,
        String note,
        Integer sortOrder
) {}
