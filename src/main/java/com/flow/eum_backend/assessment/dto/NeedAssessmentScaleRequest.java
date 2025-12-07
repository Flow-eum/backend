package com.flow.eum_backend.assessment.dto;

public record NeedAssessmentScaleRequest(
        String scaleName,  // 척도이름
        String result,     // 결과
        String note        // 비고
) {}
