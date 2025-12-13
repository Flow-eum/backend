package com.flow.eum_backend.assessment;

import com.flow.eum_backend.assessment.dto.GenogramRenderResponse;
import com.flow.eum_backend.auth.CurrentUser;
import com.flow.eum_backend.cases.CaseMemberRepository;
import com.flow.eum_backend.supervision.SupervisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cases/{caseId}/ecomap")
public class EcomapController {

    private final EcomapService ecomapService;
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
                    "이 사례의 생태도를 생성할 권한이 없습니다."
            );
        }
    }

    @PostMapping("/render")
    public ResponseEntity<GenogramRenderResponse> renderEcomap(
            @PathVariable("caseId") UUID caseId
    ) {
        UUID userId = currentUser.getUserIdOrThrow();
        checkCaseAccessOrThrow(caseId, userId);

        String url = ecomapService.renderEcomapAndGetUrl(caseId);
        return ResponseEntity.ok(new GenogramRenderResponse(url));
    }
}
