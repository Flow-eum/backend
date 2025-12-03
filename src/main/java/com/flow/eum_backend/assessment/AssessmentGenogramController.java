package com.flow.eum_backend.assessment;

import com.flow.eum_backend.ai.AiGenogramClient;
import com.flow.eum_backend.auth.CurrentUser;
import com.flow.eum_backend.cases.CaseMemberRepository;
import com.flow.eum_backend.supervision.SupervisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/cases/{caseId}/assessments")
@RequiredArgsConstructor
public class AssessmentGenogramController {

    private final AssessmentStructuredRepository assessmentStructuredRepository;
    private final AiGenogramClient aiGenogramClient;
    private final CurrentUser currentUser;
    private final CaseMemberRepository caseMemberRepository;
    private final SupervisionService supervisionService;

    private void checkCaseAccessOrThrow(UUID caseId, UUID userId) {
        boolean isMember = caseMemberRepository
                .findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .isPresent();

        boolean hasSupervision = supervisionService.hasSupervisionAccess(userId, caseId);

        if (!isMember && !hasSupervision) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "이 사례의 가계도 정보를 조회할 권한이 없습니다."
            );
        }
    }

    /*
        최신 assessment_structured.genogram_json 을 SVG로 렌더링해서 내려주는 API
     */
    @GetMapping("/genogram.svg")
    public ResponseEntity<byte[]> renderGenogram(
            @PathVariable("caseId") UUID caseId
    ) {
        UUID userId = currentUser.getUserIdOrThrow();
        checkCaseAccessOrThrow(caseId, userId);

        // case 별 최신 assessment 가져오기 (이미 repo에 메서드가 있다면 그거 사용)
        AssessmentStructured assessment = assessmentStructuredRepository
                .findFirstByCaseIdOrderByAssessmentDateDesc(caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "사정 기록을 찾을 수 없습니다."
                ));

        String genogramJson = assessment.getGenogramJson();
        if (genogramJson == null || genogramJson.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "genogram_json 이 비어 있습니다."
            );
        }

        byte[] svgBytes = aiGenogramClient.renderGenogram(genogramJson);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("image/svg+xml"));
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("genogram.svg").build()
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(svgBytes);
    }
}
