package com.flow.eum_backend.assessment.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record NeedAssessmentItemRequest(
        String category,              // "JOB", "ECONOMY", ...
        String subCategory,           // "employment_status", ...
        Map<String, Object> detail,   // 체크박스/입력값들
        Integer needLevel,            // 0~3
        Integer priority,             // 0~3
        String note
) {}