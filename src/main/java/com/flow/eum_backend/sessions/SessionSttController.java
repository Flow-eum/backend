package com.flow.eum_backend.sessions;

import com.flow.eum_backend.ai.FastApiClient;
import com.flow.eum_backend.ai.dto.SttDtos;
import com.flow.eum_backend.auth.CurrentUser;
import com.flow.eum_backend.cases.CaseMemberRepository;
import com.flow.eum_backend.infra.SupabaseStorageClient;
import com.flow.eum_backend.supervision.SupervisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases/{caseId}/sessions/{sessionId}")
@RequiredArgsConstructor
public class SessionSttController {

    private final FastApiClient fastApiClient;
    private final SupabaseStorageClient storageClient;
    private final CurrentUser currentUser;
    private final CaseMemberRepository caseMemberRepository;
    private final SupervisionService supervisionService;

    // case member or supervision APPROVED
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

    @PostMapping("/stt/transcribe")
    public ResponseEntity<SttDtos.SttResult> transcribe(
            @PathVariable("caseId") UUID caseId,
            @PathVariable("sessionId") UUID sessionId,
            @PathVariable("file") MultipartFile file
    ) throws IOException {
        UUID userId = currentUser.getUserIdOrThrow();
        checkCaseAccessOrThrow(caseId, userId);

        // 1) Supabase Storage에 오디오 업로드
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = UUID.randomUUID() + ".wav";
        }

        byte[] bytes = file.getBytes();

        String audioPath = storageClient.uploadSessionAudio(
                caseId,
                sessionId,
                originalFilename,
                bytes,
                file.getContentType()
        );

        // 2) FastAPI STT 호출
        SttDtos.SttResult result = fastApiClient.transcribe(
                bytes,
                originalFilename,
                file.getContentType()
        );

        // 필요하면 SttResult에 audioPath 필드를 추가해서 여기서 set 해줘도 됨
        // result.setAudioPath(audioPath);

        return ResponseEntity.ok(result);
    }
}
