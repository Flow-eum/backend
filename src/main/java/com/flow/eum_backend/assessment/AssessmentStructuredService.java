package com.flow.eum_backend.assessment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flow.eum_backend.assessment.dto.AssessmentFormDto;
import com.flow.eum_backend.assessment.dto.SaveAssessmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssessmentStructuredService {

    private final AssessmentStructuredRepository repository;
    private final ObjectMapper objectMapper;

    /**
     * 사정기록지 저장 (신규/수정)
     */
    public AssessmentStructured saveAssessment(UUID caseId, SaveAssessmentRequest req) {
        String json = toJson(req.getForm());

        // 같은 case + type 조합에 하나만 유지하고 싶으면 먼저 조회
        Optional<AssessmentStructured> existingOpt =
                repository.findByCaseIdAndAssessmentType(caseId, req.getAssessmentType());

        AssessmentStructured entity = existingOpt.orElseGet(AssessmentStructured::new);

        entity.setCaseId(caseId);
        entity.setAssessmentType(req.getAssessmentType());
        entity.setAssessmentDate(req.getAssessmentDate());
        entity.setPreparedByUserId(req.getPreparedByUserId());
        entity.setAssessmentJson(json);

        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(OffsetDateTime.now());
        }
        entity.setUpdatedAt(OffsetDateTime.now());

        return repository.save(entity);
    }

    /**
     * case 별 최신 사정기록지 조회 (엔티티)
     */
    public Optional<AssessmentStructured> getLatestByCaseId(UUID caseId) {
        return repository.findFirstByCaseIdOrderByAssessmentDateDesc(caseId);
    }

    /**
     * case 별 최신 사정기록지 폼 DTO 조회
     */
    public Optional<AssessmentFormDto> getLatestFormByCaseId(UUID caseId) {
        return getLatestByCaseId(caseId)
                .map(AssessmentStructured::getAssessmentJson)
                .map(this::fromJson);
    }

    private String toJson(AssessmentFormDto form) {
        try {
            return objectMapper.writeValueAsString(form);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize assessment form", e);
        }
    }

    private AssessmentFormDto fromJson(String json) {
        try {
            return objectMapper.readValue(json, AssessmentFormDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize assessment form", e);
        }
    }
}
