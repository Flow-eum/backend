package com.flow.eum_backend.ai;

import com.flow.eum_backend.ai.dto.CasePersonalDtos;
import com.flow.eum_backend.auth.CurrentUser;
import com.flow.eum_backend.cases.CaseMemberRepository;
import com.flow.eum_backend.supervision.SupervisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Currency;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases/{caseId}/similar")
@RequiredArgsConstructor
public class SimilarCaseController {

    private final SimilarCaseService similarCaseService;
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
                    "이 사례의 유사사례를 조회할 권한이 없습니다."
            );
        }
    }

    @GetMapping
    public ResponseEntity<CasePersonalDtos.CaseSimilarResponse> getSimilarCases(
            @PathVariable("caseId") UUID caseId,
            @RequestParam(name = "topK", defaultValue = "5") int topK
    ) {
        UUID userId = currentUser.getUserIdOrThrow();
        checkCaseAccessOrThrow(caseId, userId);

        var resp = similarCaseService.getSimilarCases(caseId, topK);
        return ResponseEntity.ok(resp);
    }
}
