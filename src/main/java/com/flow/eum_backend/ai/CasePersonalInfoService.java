package com.flow.eum_backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flow.eum_backend.ai.dto.CasePersonalDtos;
import com.flow.eum_backend.assessment.AssessmentStructured;
import com.flow.eum_backend.assessment.AssessmentStructuredRepository;
import com.flow.eum_backend.assessment.NeedAssessment;
import com.flow.eum_backend.assessment.NeedAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CasePersonalInfoService {

    private final AssessmentStructuredRepository assessmentRepository;
    private final NeedAssessmentRepository needAssessmentRepository;
    private final FastApiClient fastApiClient;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public CasePersonalDtos.CasePersonalSaveResponse syncCasePersonalInfo(UUID caseId) {

        // 1) 최신 사정기록 조회
        AssessmentStructured latestAssessment =
                assessmentRepository.findFirstByCaseIdOrderByAssessmentDateDesc(caseId)
                        .orElse(null);

        // 2) 최신 욕구사정 조회
        NeedAssessment latestNeedAssessment =
                needAssessmentRepository.findFirstByCaseIdOrderByAssessmentDateDescCreatedAtDesc(caseId)
                        .orElse(null);

        // 둘 다 없으면 굳이 FastAPI에 보낼 필요가 없으니 예외
        if (latestAssessment == null && latestNeedAssessment == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "해당 사례의 사정/욕구사정 기록이 없습니다."
            );
        }

        // 3) FastAPI에 보낼 통합 payload 구성
        ObjectNode root = objectMapper.createObjectNode();
        root.put("case_id", caseId.toString());

        // --- 사정기록지 쪽 ---
        if (latestAssessment != null) {
            root.put("assessment_id", latestAssessment.getId().toString());
            root.put("assessment_type", latestAssessment.getAssessmentType());
            root.put("assessment_date", latestAssessment.getAssessmentDate().toString());

            // 사정기록지의 form JSON (clientBasic, familyMembers 등)
            String formJson = latestAssessment.getAssessmentJson(); // String 타입이라고 가정
            if (formJson != null && !formJson.isBlank()) {
                try {
                    JsonNode formNode = objectMapper.readTree(formJson);
                    root.set("assessment_form", formNode);
                } catch (Exception e) {
                    root.put("assessment_form_raw", formJson);
                }
            }
        }

        // --- 욕구사정 쪽 ---
        if (latestNeedAssessment != null) {
            root.put("need_assessment_id", latestNeedAssessment.getId().toString());
            root.put("need_assessment_date", latestNeedAssessment.getAssessmentDate().toString());
            root.put("need_assessment_type", latestNeedAssessment.getAssessmentType());

            // NeedAssessment 엔티티 전체를 JSON으로 직렬화 (items, scales 포함)
            JsonNode needNode = objectMapper.valueToTree(latestNeedAssessment);
            root.set("need_assessment", needNode);
        }

        // TODO: 여기서 age, gender, job, disability, family_type, residence, long_term_care 등
        //       k-NN feature로 쓸 필드를 평탄화해서 추가해도 됨.
        //       ex) root.put("age", ...);

        // 4) FastAPI로 전송 (이제 모든 코드 경로에서 반드시 return 됨)
        return fastApiClient.saveCasePersonal(root);
    }
}