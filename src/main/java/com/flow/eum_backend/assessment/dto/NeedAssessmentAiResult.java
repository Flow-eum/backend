package com.flow.eum_backend.assessment.dto;

import java.util.List;
import java.util.Map;

public record NeedAssessmentAiResult(
        String riskLevel,
        List<String> highPriorityDomains,
        String summary,
        Map<String, Object> raw
) {
}
