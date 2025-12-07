package com.flow.eum_backend.assessment;

import com.flow.eum_backend.assessment.dto.NeedAssessmentCreateRequest;
import com.flow.eum_backend.assessment.dto.NeedAssessmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cases/{caseId}/need-assessments")
public class NeedAssessmentController {

    private final NeedAssessmentService needAssessmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NeedAssessmentResponse create(
            @PathVariable("caseId") UUID caseId,
            @RequestBody NeedAssessmentCreateRequest request
    ) {
        return needAssessmentService.create(caseId, request);
    }

    @GetMapping("/latest")
    public NeedAssessmentResponse getLatest(
            @PathVariable("caseId") UUID caseId
    ) {
        return needAssessmentService.getLatest(caseId);
    }
}
