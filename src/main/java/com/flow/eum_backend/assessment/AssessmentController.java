package com.flow.eum_backend.assessment;

import com.flow.eum_backend.assessment.dto.AssessmentFormDto;
import com.flow.eum_backend.assessment.dto.SaveAssessmentRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cases/{caseId}/assessments")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentStructuredService assessmentService;

    @PostMapping
    public ResponseEntity<Void> saveAssessment(
            @PathVariable("caseId") UUID caseId,
            @RequestBody SaveAssessmentRequest request
    ) {
        assessmentService.saveAssessment(caseId, request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/latest")
    public ResponseEntity<AssessmentFormDto> getLatestAssessment(
            @PathVariable("caseId") UUID caseId
    ) {
        return assessmentService.getLatestFormByCaseId(caseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
