package com.flow.eum_backend.assessment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flow.eum_backend.ai.CasePersonalInfoService;
import com.flow.eum_backend.assessment.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NeedAssessmentService {

    private final NeedAssessmentRepository assessmentRepository;
    private final ObjectMapper objectMapper;
    private final CasePersonalInfoService casePersonalInfoService;

    @Transactional
    public NeedAssessmentResponse create(UUID caseId, NeedAssessmentCreateRequest request) {
        // TODO: 여기서 caseId 접근 권한 체크 (case_member / supervision 룰) 넣어주면 좋음.

        NeedAssessment assessment = NeedAssessment.builder()
                .caseId(caseId)
                .assessmentType(request.assessmentType())
                .assessmentDate(request.assessmentDate())
                .preparedByUserId(request.preparedByUserId())
                .note(request.note())
                .build();

        // items
        int itemOrder = 0;
        if (request.items() != null) {
            for (NeedAssessmentItemRequest itemReq : request.items()) {
                NeedAssessmentItem item = NeedAssessmentItem.builder()
                        .assessment(assessment)
                        .category(itemReq.category())
                        .subCategory(itemReq.subCategory())
                        .detailJson(writeJson(itemReq.detail()))
                        .needLevel(itemReq.needLevel())
                        .priority(itemReq.priority())
                        .note(itemReq.note())
                        .sortOrder(itemOrder++)
                        .build();
                assessment.getItems().add(item);
            }
        }

        // scales
        int scaleOrder = 0;
        if (request.scales() != null) {
            for (NeedAssessmentScaleRequest scaleReq : request.scales()) {
                NeedAssessmentScale scale = NeedAssessmentScale.builder()
                        .assessment(assessment)
                        .scaleName(scaleReq.scaleName())
                        .result(scaleReq.result())
                        .note(scaleReq.note())
                        .sortOrder(scaleOrder++)
                        .build();
                assessment.getScales().add(scale);
            }
        }

        NeedAssessment saved = assessmentRepository.save(assessment);

        casePersonalInfoService.syncCasePersonalInfo(caseId);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public NeedAssessmentResponse getLatest(UUID caseId) {
        // TODO: 이쪽도 case 접근 권한 체크
        NeedAssessment assessment = assessmentRepository
                .findFirstByCaseIdOrderByAssessmentDateDescCreatedAtDesc(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Need assessment not found"));

        // LAZY 컬렉션 초기화용 접근 (트랜잭션 안이니까 단순 size() 만 호출해도 됨)
        assessment.getItems().size();
        assessment.getScales().size();

        return toResponse(assessment);
    }

    // ---------- 내부 helper ----------

    private String writeJson(Map<String, Object> map) {
        if (map == null) return null;
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("detail JSON 직렬화 실패", e);
        }
    }

    private Map<String, Object> readJson(String json) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("detail JSON 역직렬화 실패", e);
        }
    }

    private NeedAssessmentResponse toResponse(NeedAssessment entity) {
        List<NeedAssessmentItemResponse> itemResponses = new ArrayList<>();
        for (NeedAssessmentItem item : entity.getItems()) {
            itemResponses.add(new NeedAssessmentItemResponse(
                    item.getId(),
                    item.getCategory(),
                    item.getSubCategory(),
                    readJson(item.getDetailJson()),
                    item.getNeedLevel(),
                    item.getPriority(),
                    item.getNote(),
                    item.getSortOrder()
            ));
        }

        List<NeedAssessmentScaleResponse> scaleResponses = new ArrayList<>();
        for (NeedAssessmentScale scale : entity.getScales()) {
            scaleResponses.add(new NeedAssessmentScaleResponse(
                    scale.getId(),
                    scale.getScaleName(),
                    scale.getResult(),
                    scale.getNote(),
                    scale.getSortOrder()
            ));
        }

        return new NeedAssessmentResponse(
                entity.getId(),
                entity.getCaseId(),
                entity.getAssessmentType(),
                entity.getAssessmentDate(),
                entity.getPreparedByUserId(),
                entity.getNote(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                itemResponses,
                scaleResponses
        );
    }
}
