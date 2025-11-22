package com.flow.eum_backend.supervision;

import com.flow.eum_backend.supervision.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@Tag(name = "Supervision", description = "슈퍼비전 요청 및 키 공유 API")
@SecurityRequirement(name = "bearerAuth")
public class SupervisionController {

    private final SupervisionService supervisionService;

    public SupervisionController(SupervisionService supervisionService) {
        this.supervisionService = supervisionService;
    }

    @PostMapping("/api/cases/{caseId}/supervision/requests")
    @Operation(
            summary = "슈퍼비전 요청 생성",
            description = """
                    특정 사례에 대해 슈퍼비전을 요청합니다.
                    - 현재 로그인한 사용자가 요청자가 되며,
                      supervisorUserId 에 지정한 사용자에게 요청이 전달됩니다.
                    """
    )
    public ResponseEntity<SupervisionRequestDto> createRequest(
            @PathVariable("caseId") UUID caseId,
            @RequestBody SupervisionRequestCreateRequest request
    ) {
        return ResponseEntity.ok(supervisionService.createRequest(caseId, request));
    }

    @GetMapping("/api/supervision/requests/as-requester")
    @Operation(summary = "내가 요청자료 보낸 슈퍼비전 요청 목록")
    public ResponseEntity<List<SupervisionRequestDto>> listMyRequestsAsRequester() {
        return ResponseEntity.ok(supervisionService.listMyRequestsAsRequester());
    }

    @GetMapping("/api/supervision/requests/as-supervisor")
    @Operation(summary = "내가 슈퍼바이저로 받은 슈퍼비전 요청 목록")
    public ResponseEntity<List<SupervisionRequestDto>> listMyRequestsAsSupervisor() {
        return ResponseEntity.ok(supervisionService.listMyRequestsAsSupervisor());
    }

    @PatchMapping("/api/supervision/requests/{requestId}")
    @Operation(
            summary = "슈퍼비전 요청 상태 변경",
            description = """
                    - supervisor: pending → approved / rejected
                    - requester : pending/approved → revoked
                    """
    )
    public ResponseEntity<SupervisionRequestDto> updateRequestStatus(
            @PathVariable("requestId") UUID requestId,
            @RequestBody SupervisionRequestUpdateStatusRequest request
    ) {
        return ResponseEntity.ok(supervisionService.updateRequestStatus(requestId, request));
    }

    // ---------- 5) share 생성 ----------

    @PostMapping("/api/supervision/requests/{requestId}/shares")
    @Operation(
            summary = "슈퍼비전 share 생성",
            description = """
                    승인된 슈퍼비전 요청에 대해,
                    특정 회기 또는 문서의 DEK(문서 키)를 공유합니다.
                    - recordType: "session_record" 또는 "document"
                    - recordId  : session_record_meta.id 또는 case_document_meta.id
                    """
    )
    public ResponseEntity<SupervisionShareDto> createShare(
            @PathVariable("requestId") UUID requestId,
            @RequestBody SupervisionShareCreateRequest request
    ) {
        return ResponseEntity.ok(supervisionService.createShare(requestId, request));
    }

    // ---------- 6) share 목록 조회 ----------

    @GetMapping("/api/supervision/requests/{requestId}/shares")
    @Operation(
            summary = "특정 슈퍼비전 요청에 대한 share 목록 조회",
            description = "요청자와 슈퍼바이저 둘 다 이 목록을 조회할 수 있습니다."
    )
    public ResponseEntity<List<SupervisionShareDto>> listShares(
            @PathVariable("requestId") UUID requestId
    ) {
        return ResponseEntity.ok(supervisionService.listShares(requestId));
    }

}
