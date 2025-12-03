package com.flow.eum_backend.supervision;

import com.flow.eum_backend.supervision.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supervision")
@RequiredArgsConstructor
@Tag(name = "Supervision", description = "슈퍼비전(열람 요청/승인) 관련 api")
@SecurityRequirement(name = "bearerAuth")
public class SupervisionController {

    private final SupervisionService supervisionService;

    @PostMapping("/requests")
    @Operation(summary = "슈퍼비전 열람 요청 보내기 (A -> B)")
    public ResponseEntity<SupervisionRequestResponse> requestView(
            @RequestBody SupervisionRequestCreateRequest request
    ) {
        return ResponseEntity.ok(supervisionService.requestView(request));
    }

    @GetMapping("/requests/incoming")
    @Operation(summary = "내게 들어온 PENDING 슈퍼비전 요청 목록 (B용)")
    public ResponseEntity<List<SupervisionRequestResponse>> getIncoming() {
        return ResponseEntity.ok(supervisionService.listIncomingPending());
    }

    @GetMapping("/requests/mine")
    @Operation(summary = "내가 보낸 슈퍼비전 요청 목록 (A용)")
    public ResponseEntity<List<SupervisionRequestResponse>> getMine() {
        return ResponseEntity.ok(supervisionService.listMyRequests());
    }

    @PostMapping("/requests/{requestId}/approve")
    @Operation(summary = "슈퍼비전 요청 승인 (B용)")
    public ResponseEntity<SupervisionRequestResponse> approve(
            @PathVariable UUID requestId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime allowedFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime allowedUntil
    ) {
        return ResponseEntity.ok(
                supervisionService.approve(requestId, allowedFrom, allowedUntil)
        );
    }

    @PostMapping("/requests/{requestId}/reject")
    @Operation(summary = "슈퍼비전 요청 거절 (B용)")
    public ResponseEntity<SupervisionRequestResponse> reject(
            @PathVariable UUID requestId
    ) {
        return ResponseEntity.ok(supervisionService.reject(requestId));
    }
}
