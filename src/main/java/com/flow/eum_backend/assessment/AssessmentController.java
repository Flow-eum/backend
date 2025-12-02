package com.flow.eum_backend.assessment;

import com.flow.eum_backend.assessment.dto.AssessmentFormDto;
import com.flow.eum_backend.assessment.dto.SaveAssessmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/cases/{caseId}/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentStructuredService assessmentService;

    public ResponseEntity<Void> saveAssessment(
            @PathVariable("caseId") UUID caseId,
            @RequestBody SaveAssessmentRequest request
    ) {
        assessmentService.saveAssessment(caseId, request);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<AssessmentFormDto> getLatestAssessment(
            @PathVariable("caseId") UUID caseId
    ) {
        return assessmentService.getLatestFormByCaseId(caseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
