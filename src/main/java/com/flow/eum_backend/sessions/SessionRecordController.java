package com.flow.eum_backend.sessions;

import com.flow.eum_backend.sessions.dto.SessionRecordCreateRequest;
import com.flow.eum_backend.sessions.dto.SessionRecordDetailDto;
import com.flow.eum_backend.sessions.dto.SessionRecordSummaryDto;
import com.flow.eum_backend.sessions.dto.SessionRecordUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/*
    상담 회차(session_record_meta) 관련 컨트롤러
 */
@RestController
@RequestMapping("/api/cases/{caseId}/sessions")
@Tag(name = "SessionRecords", description = "사례별 상담 회차 메타 API")
@SecurityRequirement(name = "bearerAuth")
public class SessionRecordController {

    private final SessionRecordService sessionRecordService;

    public SessionRecordController(SessionRecordService sessionRecordService) {
        this.sessionRecordService = sessionRecordService;
    }

    @PostMapping
    @Operation(
            summary = "새 상담 회차 메타 생성",
            description = """
                    특정 사례에 대해 새 상담 회차 메타를 생성합니다.
                    - seq 를 보내지 않으면 서버에서 자동으로 다음 회차 번호를 부여합니다.
                    - s3Key / contentSha256 / sizeBytes 는
                      이미 암호화+업로드된 파일의 메타 정보입니다.
                    """
    )
    public ResponseEntity<SessionRecordDetailDto> createSessionRecord(
            @PathVariable("caseId") UUID caseId,
            @RequestBody SessionRecordCreateRequest request
    ) {
        SessionRecordDetailDto dto = sessionRecordService.createSessionRecord(caseId, request);

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(
            summary = "사례별 상담 회차 목록 조회",
            description = "해당 사례에 등록된 모든 상담 회차 메타를 seq 오름차순으로 반환합니다."
    )
    public ResponseEntity<List<SessionRecordSummaryDto>> listSessions(
            @PathVariable("caseId") UUID caseId
    ) {
        List<SessionRecordSummaryDto> list =
                sessionRecordService.listSessionByCase(caseId);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{sessionId}")
    @Operation(
            summary = "상담 회차 메타 상세 조회",
            description = "단일 상담 회차의 메타 정보를 반환합니다."
    )
    public ResponseEntity<SessionRecordDetailDto> getSessionDetail(
            @PathVariable("caseId") UUID caseId,
            @PathVariable("sessionId") UUID sessionId
    ) {
        SessionRecordDetailDto dto =
                sessionRecordService.getSessionDetail(caseId, sessionId);

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<SessionRecordDetailDto> updateSession(
            @PathVariable("caseId") UUID caseId,
            @PathVariable("sessionId") UUID sessionId,
            @RequestBody SessionRecordUpdateRequest request
    ) {
        SessionRecordDetailDto dto =
                sessionRecordService.updateSession(caseId, sessionId, request);

        return ResponseEntity.ok(dto);
    }
}
