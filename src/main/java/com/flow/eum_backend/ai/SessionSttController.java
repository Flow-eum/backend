package com.flow.eum_backend.ai;

import com.flow.eum_backend.ai.dto.SttDtos;
import com.flow.eum_backend.auth.CurrentUser;
import com.flow.eum_backend.cases.CaseMemberRepository;
import com.flow.eum_backend.supervision.SupervisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/cases/{caseId}/sessions/{sessionId}")
@RequiredArgsConstructor
public class SessionSttController {

    private final AiSttClient aiSttClient;
    private final CurrentUser currentUser;
    private final CaseMemberRepository caseMemberRepository;
    private final SupervisionService supervisionService;

    // 공통 권한 체크: case 멤버이거나, supervision APPROVED 면 OK
    private void checkCaseAccessOrThrow(UUID caseId, UUID userId) {
        boolean isMember = caseMemberRepository
                .findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .isPresent();

        boolean hasSupervision = supervisionService.hasSupervisionAccess(userId, caseId);

        if (!isMember && !hasSupervision) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "이 사례의 STT 기능을 사용할 권한이 없습니다."
            );
        }
    }

    /*
        stt: 세션별 녹음파일 -> 텍스트
     */
    @PostMapping("/stt/transcribe")
    public ResponseEntity<SttDtos.SttResult> transcribe(
            @PathVariable("caseId") UUID caseId,
            @PathVariable("sessionId") UUID sessionId,
            @RequestPart("file") MultipartFile file
    ) {
        UUID userId = currentUser.getUserIdOrThrow();
        checkCaseAccessOrThrow(caseId, userId);

        // 지금은 녹음파일은 별도로 저장하지 않고,
        // FastAPI에만 보내서 텍스트를 받아온 뒤 그대로 반환.
        SttDtos.SttResult result = aiSttClient.transcribe(file);
        return ResponseEntity.ok(result);
    }
}
