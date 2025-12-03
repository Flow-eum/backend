package com.flow.eum_backend.sessions;

import com.flow.eum_backend.sessions.dto.SaveAiOutputRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases/{caseId}/sessions/{sessionId}/ai-outputs")
@RequiredArgsConstructor
@Tag(name = "Sessions - Ai Outputs", description = "세션별 Ai 결과 저장/조회 API")
@SecurityRequirement(name = "bearerAuth")
public class SessionAiOutputController {

    private final SessionRecordService sessionRecordService;

    @PostMapping
    @Operation(
            summary = "세션 AI 결과 저장/갱신",
            description = """
                    특정 상담 회차(session)에 대한 AI 결과를 저장하거나 갱신합니다.
                    동일한 key가 이미 존재하면 덮어씁니다.
                    
                    예시 key:
                      - "progress_report_md"      : 개입 경과 보고(마크다운)
                      - "realtime_summary"        : 실시간 상담 요약
                      - "summary_report"          : 요약 리포트
                      - "supervision_draft"       : 슈퍼비전 초안
                      - "transcribed_note_md"     : 녹음 → 과정기록지(마크다운)
                    """
    )
    public ResponseEntity<Void> saveAiOutput(
            @PathVariable("caseId") UUID caseId,
            @PathVariable("sessionId") UUID sessionId,
            @RequestBody SaveAiOutputRequest request
    ) {
        sessionRecordService.saveAiOutput(caseId, sessionId, request);

        return ResponseEntity.ok().build();
    }

    /*
        세션별 Ai 결과 전체 조회
     */
    @GetMapping
    @Operation(
            summary = "세션 AI 결과 전체 조회",
            description = "특정 상담 회차에 저장된 ai_outputs 전체를 Map 형태로 반환합니다."
    )
    public ResponseEntity<AiOutputsResponse> getAiOutputs(
            @PathVariable("caseId") UUID caseId,
            @PathVariable("sessionId") UUID sessionId
    ) {
        AiOutputsResponse response = sessionRecordService.getAiOutputs(caseId, sessionId);

        return ResponseEntity.ok(response);
    }

    /*
        세션별 Ai 결과 중 특정 key만 조회
     */
    @GetMapping("/{key}")
    @Operation(
            summary = "세션 Ai 결과 중 특정 key 조회",
            description = "ai_outputs 중 지정한 key에 해당하는 값만 단일로 반환합니다."
    )
    public ResponseEntity<Object> getAiOutputByKey(
            @PathVariable("caseID") UUID caseId,
            @PathVariable("sessionId") UUID sessionId,
            @PathVariable("key") String key
    ) {
        Optional<Object> valueOpt =
                sessionRecordService.getAiOutputByKey(caseId, sessionId, key);

        return valueOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

