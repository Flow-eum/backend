package com.flow.eum_backend.cases;

import com.flow.eum_backend.cases.dto.CaseCreateRequest;
import com.flow.eum_backend.cases.dto.CaseDetailDto;
import com.flow.eum_backend.cases.dto.CaseSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/*
    사례(cases) 관련 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/cases")
@Tag(name = "Cases", description = "사례(case) 관리 api")
@SecurityRequirement(name = "bearerAuth") // Swagger에서 JWT 필요하다고 표시
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping
    @Operation(
            summary = "새 사례 생성",
            description = """
                    새로운 사례를 생성합니다.
                    - displayCode를 보내지 않으면 자동으로 서버에서 생성
                    - 생성한 사용자는 자동으로 primary_counselor로 등록
                    """
    )
    public ResponseEntity<CaseDetailDto> createCase(
            @RequestBody CaseCreateRequest request
    ) {
        CaseDetailDto dto = caseService.createCase(request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(
            summary = "내 사례 목록 조회",
            description = "현재 로그인한 사용자가 멤버로 참여 중인 사례 목록 반환"
    )
    public ResponseEntity<List<CaseSummaryDto>> listMyCases() {
        List<CaseSummaryDto> list = caseService.listMyCases();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{caseId}")
    @Operation(
            summary = "사례 상세 조회",
            description = "지정한 사례의 기본 정보와 멤버 정보 반환. 멤버가 아니면 403 응답"
    )
    public ResponseEntity<CaseDetailDto> getCaseDetail(
            @PathVariable("caseId") UUID caseId
    ) {
        CaseDetailDto dto = caseService.getCaseDetail(caseId);
        return ResponseEntity.ok(dto);
    }
}
