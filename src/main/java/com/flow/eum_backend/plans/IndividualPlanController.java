package com.flow.eum_backend.plans;

import com.flow.eum_backend.plans.dto.IndividualPlanCreateRequest;
import com.flow.eum_backend.plans.dto.IndividualPlanDetailDto;
import com.flow.eum_backend.plans.dto.IndividualPlanSummaryDto;
import com.flow.eum_backend.plans.dto.IndividualPlanUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases/{caseId}/plans")
@Tag(name = "IndividualPlans", description = "개입 계획 메타 API")
@SecurityRequirement(name = "bearerAuth")
public class IndividualPlanController {

    private final IndividualPlanService planService;

    public IndividualPlanController(IndividualPlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    @Operation(
            summary = "새 개입 계획 버전 생성",
            description = """
                    특정 사례에 대한 개입 계획을 새 버전으로 등록합니다.
                    - version_no 는 서버에서 자동으로 (기존 max + 1) 으로 부여합니다.
                    - 일반적으로 planStatus 는 "draft" 로 시작한 뒤,
                      추후 "active" 로 변경할 수 있습니다.
                    """
    )
    public ResponseEntity<IndividualPlanDetailDto> createPlan(
            @PathVariable("caseId") UUID caseId,
            @RequestBody IndividualPlanCreateRequest request
    ) {
        return ResponseEntity.ok(planService.createPlan(caseId, request));
    }

    @GetMapping
    @Operation(summary = "사례별 개입 계획 목록 조회")
    public ResponseEntity<List<IndividualPlanSummaryDto>> listPlans(
            @PathVariable("caseId") UUID caseId
    ) {
        return ResponseEntity.ok(planService.listPlans(caseId));
    }

    @GetMapping("/{planId}")
    @Operation(summary = "개입 계획 상세 조회")
    public ResponseEntity<IndividualPlanDetailDto> getPlan(
            @PathVariable("caseId") UUID caseId,
            @PathVariable("planId") UUID planId
    ) {
        return ResponseEntity.ok(planService.getPlan(caseId, planId));
    }

    @PatchMapping("/{planId}")
    @Operation(summary = "개입 계획 메타 수정")
    public ResponseEntity<IndividualPlanDetailDto> updatePlan(
            @PathVariable("caseId") UUID caseId,
            @PathVariable("planId") UUID planId,
            @RequestBody IndividualPlanUpdateRequest request
    ) {
        return ResponseEntity.ok(planService.updatePlan(caseId, planId, request));
    }

    @GetMapping("/active")
    @Operation(
            summary = "active 상태의 개입 계획 조회",
            description = "plan_status가 'active'인 최신 버전 계획을 하나 반환합니다."
    )
    public ResponseEntity<IndividualPlanDetailDto> getActivePlan(
            @PathVariable("caseId") UUID caseId
    ) {
        return ResponseEntity.ok(planService.getActivePlan(caseId));
    }
}
